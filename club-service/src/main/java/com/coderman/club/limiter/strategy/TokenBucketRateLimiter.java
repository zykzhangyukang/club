package com.coderman.club.limiter.strategy;

import com.coderman.club.limiter.RateLimiter;
import com.coderman.club.limiter.properties.TokenBucketProperties;
import com.coderman.club.limiter.properties.LimitProperties;
import com.coderman.club.service.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 令牌桶限流算法
 * @author ：zhangyukang
 * @date ：2024/06/04 12:20
 */
@Component
@Slf4j
public class TokenBucketRateLimiter implements RateLimiter {

    @Resource
    private RedisService redisService;

    @Override
    public boolean allowRequest(String resolve, LimitProperties limitProperties) {

        TokenBucketProperties tokenBucketProperties = (TokenBucketProperties)limitProperties;

        List<String> keys = getKeys(resolve);

        // 根据限流时间维度计算时间
        long timeLong = getCurrentTimeLong(tokenBucketProperties.getTimeUnit());

        // The arguments to the LUA script. time() returns unixTime in seconds.
        List<String> scriptArgs = Arrays.asList(tokenBucketProperties.getReplenishRate() + "",
                tokenBucketProperties.getBurstCapacity() + "",
                (Instant.now().toEpochMilli() / timeLong) + "",
                "1",
                timeLong + "");

        // 第一个参数是是否被限流，第二个参数是剩余令牌数
        List<Long> rateLimitResponse = this.redisService.executeLuaScript( Long.class ,"lua/TokenBucketRate.lua", keys, scriptArgs.toArray());

        Assert.notNull(rateLimitResponse, "redis execute redis lua limit failed.");
        Long isAllowed = rateLimitResponse.get(0);
        Long newTokens = rateLimitResponse.get(1);

        log.info("rate limit key [{}] result: isAllowed [{}] new tokens [{}].", resolve, isAllowed, newTokens);

        return isAllowed >0;
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
}
