package com.coderman.club.aop;

import com.coderman.club.exception.RateLimitException;
import com.coderman.club.annotation.RateLimit;
import com.coderman.club.limiter.properties.DefaultLimitProperties;
import com.coderman.club.limiter.properties.LimitProperties;
import com.coderman.club.limiter.resolver.KeyResolver;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.utils.HttpContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 */
@Slf4j
@Aspect
@Component
@Order(value = 50)
public class RateLimitAspect implements ApplicationContextAware {

    @Resource
    private RedisService redisService;

    private ApplicationContext applicationContext;

    @Around("execution(public * *(..)) && @annotation(com.coderman.club.annotation.RateLimit)")
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        // 断言不会被限流
        this.assertNonLimit(rateLimit, pjp);

        return pjp.proceed();
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void assertNonLimit(RateLimit rateLimit, ProceedingJoinPoint pjp) {

        Class<? extends KeyResolver> keyResolverClazz = rateLimit.keyResolver();
        KeyResolver keyResolver = applicationContext.getBean(keyResolverClazz);
        String resolve = keyResolver.resolve(HttpContextUtil.getHttpServletRequest(), pjp);
        List<String> keys = getKeys(resolve);

        LimitProperties limitProperties = getLimitProperties(rateLimit);

        // 根据限流时间维度计算时间
        long timeLong = getCurrentTimeLong(limitProperties.timeUnit());

        // The arguments to the LUA script. time() returns unixTime in seconds.
        List<String> scriptArgs = Arrays.asList(limitProperties.replenishRate() + "",
                limitProperties.burstCapacity() + "",
                (Instant.now().toEpochMilli() / timeLong) + "",
                "1",
                timeLong + "");

        // 第一个参数是是否被限流，第二个参数是剩余令牌数
        List<Long> rateLimitResponse = this.redisService.executeLuaScript(this.getLuaScript(), keys, scriptArgs.toArray());

        Assert.notNull(rateLimitResponse, "redis execute redis lua limit failed.");
        Long isAllowed = rateLimitResponse.get(0);
        Long newTokens = rateLimitResponse.get(1);

        log.info("rate limit key [{}] result: isAllowed [{}] new tokens [{}].", resolve, isAllowed, newTokens);

        if (isAllowed <= 0) {

            throw new RateLimitException(resolve);
        }
    }

    private LimitProperties getLimitProperties(RateLimit rateLimit) {

        Class<? extends LimitProperties> aClass = rateLimit.limitProperties();

        if (aClass == DefaultLimitProperties.class) {

            return new DefaultLimitProperties(rateLimit.replenishRate(), rateLimit.burstCapacity(), rateLimit.timeUnit());
        }

        return applicationContext.getBean(aClass);
    }

    private long getCurrentTimeLong(TimeUnit timeUnit) {
        switch (timeUnit) {
            case SECONDS:
                return 1000;
            case MINUTES:
                return 1000 * 60;
            case HOURS:
                return 1000 * 60 * 60;
            case DAYS:
                return 1000 * 60 * 60 * 24;
            default:
                throw new IllegalArgumentException("timeUnit:" + timeUnit + " not support");
        }
    }

    private List<String> getKeys(String id) {
        // use `{}` around keys to use Redis Key hash tags
        // this allows for using redis cluster

        // Make a unique key per user.
        String prefix = "request_rate_limiter.{" + id;

        // You need two Redis keys for Token Bucket.
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }

    private String getLuaScript() {
        return "local tokens_key = KEYS[1]\n" +
                "local timestamp_key = KEYS[2]\n" +
                "local rate = tonumber(ARGV[1])\n" +
                "local capacity = tonumber(ARGV[2])\n" +
                "local now = tonumber(ARGV[3])\n" +
                "local requested = tonumber(ARGV[4])\n" +
                "local fill_time = capacity/rate\n" +
                "local ttl = math.floor(fill_time*2)\n" +
                "local last_tokens = tonumber(redis.call(\"get\", tokens_key))\n" +
                "if last_tokens == nil then\n" +
                "  last_tokens = capacity\n" +
                "end\n" +
                "local last_refreshed = tonumber(redis.call(\"get\", timestamp_key))\n" +
                "if last_refreshed == nil then\n" +
                "  last_refreshed = 0\n" +
                "end\n" +
                "local delta = math.max(0, now-last_refreshed)\n" +
                "local filled_tokens = math.min(capacity, last_tokens+(delta*rate))\n" +
                "local allowed = filled_tokens >= requested\n" +
                "local new_tokens = filled_tokens\n" +
                "local allowed_num = 0\n" +
                "if allowed then\n" +
                "  new_tokens = filled_tokens - requested\n" +
                "  allowed_num = 1\n" +
                "end\n" +
                "if ttl > 0 then\n" +
                "  redis.call(\"setex\", tokens_key, ttl, new_tokens)\n" +
                "  redis.call(\"setex\", timestamp_key, ttl, now)\n" +
                "end\n" +
                "return { allowed_num, new_tokens }";
    }
}