package com.coderman.club.aop;

import com.coderman.club.annotation.RateLimit;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.constant.user.CommonConst;
import com.coderman.club.exception.RateLimitException;
import com.coderman.club.limiter.properties.SlidingWindowLimitProperties;
import com.coderman.club.limiter.strategy.FixedWindowRateLimiter;
import com.coderman.club.limiter.RateLimiter;
import com.coderman.club.limiter.strategy.SlidingWindowRateLimiter;
import com.coderman.club.limiter.strategy.TokenBucketRateLimiter;
import com.coderman.club.limiter.LimiterStrategy;
import com.coderman.club.limiter.properties.TokenBucketProperties;
import com.coderman.club.limiter.properties.FixedWindowLimitProperties;
import com.coderman.club.limiter.properties.LimitProperties;
import com.coderman.club.limiter.resolver.KeyResolver;
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

import java.lang.reflect.Method;

/**
 * @author zhangyukang
 */
@Slf4j
@Aspect
@Component
@Order(value = 50)
public class RateLimitAspect implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Around("execution(public * *(..)) && @annotation(com.coderman.club.annotation.RateLimit)")
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        // 获取限流器
        RateLimiter rateLimiter = getRateLimiter(rateLimit);

        // 断言不会被限流
        this.assertNonLimit(rateLimiter, rateLimit, pjp);

        return pjp.proceed();
    }

    private RateLimiter getRateLimiter(RateLimit rateLimit) {
        switch (rateLimit.strategy()) {
            case LimiterStrategy.TOKEN_BUCKET:
                return applicationContext.getBean(TokenBucketRateLimiter.class);
            case LimiterStrategy.FIXED_WINDOW:
                return applicationContext.getBean(FixedWindowRateLimiter.class);
            case LimiterStrategy.SLIDING_WINDOW:
                return applicationContext.getBean(SlidingWindowRateLimiter.class);
            default:
                throw new IllegalArgumentException("Unsupported rate limiting strategy: " + rateLimit.strategy());
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void assertNonLimit(RateLimiter rateLimiter, RateLimit rateLimit, ProceedingJoinPoint pjp) {

        Class<? extends KeyResolver> keyResolverClazz = rateLimit.keyResolver();
        KeyResolver keyResolver = applicationContext.getBean(keyResolverClazz);
        String resolve = keyResolver.resolve(HttpContextUtil.getHttpServletRequest(), pjp);

        // 设置前缀
        resolve  = RedisKeyConstant.RATE_LIMIT + resolve;

        LimitProperties limitProperties = getLimitProperties(rateLimit);

        boolean isAllowed = rateLimiter.allowRequest(resolve, limitProperties);
        if (!isAllowed) {
            throw new RateLimitException("访问过于频繁！");
        }
    }

    private LimitProperties getLimitProperties(RateLimit rateLimit) {

        String strategy = rateLimit.strategy();

        if (LimiterStrategy.TOKEN_BUCKET.equals(strategy)) {
            return new TokenBucketProperties(rateLimit.replenishRate(), rateLimit.burstCapacity(), rateLimit.timeUnit());
        } else if (LimiterStrategy.FIXED_WINDOW.equals(strategy)) {
            return new FixedWindowLimitProperties(rateLimit.windowSize(), rateLimit.windowRequests(), rateLimit.timeUnit());
        } else if(LimiterStrategy.SLIDING_WINDOW.equals(strategy)){
            return new SlidingWindowLimitProperties(rateLimit.windowSize(), rateLimit.windowRequests(), rateLimit.timeUnit());
        }

        throw new IllegalArgumentException("getLimitProperties strategy not support ");
    }
}