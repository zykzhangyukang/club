package com.coderman.club.limiter.strategy;

import com.coderman.club.limiter.RateLimiter;
import com.coderman.club.limiter.properties.LimitProperties;
import com.coderman.club.limiter.properties.SlidingWindowLimitProperties;
import com.coderman.club.service.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author ：zhangyukang
 * @date ：2024/06/04 17:30
 */
@Component
@Slf4j
public class SlidingWindowRateLimiter implements RateLimiter {

    @Resource
    private RedisService redisService;

    @Override
    public boolean allowRequest(String resolve, LimitProperties properties) {
        SlidingWindowLimitProperties windowProperties = (SlidingWindowLimitProperties) properties;

        // 获取限流时间窗口配置
        int windowSize = windowProperties.getWindowSize();
        int windowRequests = windowProperties.getWindowRequests();
        TimeUnit timeUnit = windowProperties.getTimeUnit();

        // 计算窗口大小的毫秒数
        long windowInMillis = timeUnit.toMillis(windowSize);
        long currentTime = System.currentTimeMillis();

        // 构建 Lua 脚本参数
        List<String> scriptArgs = Arrays.asList(
                String.valueOf(windowRequests),
                String.valueOf(windowInMillis),
                String.valueOf(currentTime)
        );

        // 执行 Lua 脚本
        List<Long> result = redisService.executeLuaScript(Long.class, "lua/SlidingWindowRate.lua", Collections.singletonList(resolve), scriptArgs.toArray());
        Assert.notNull(result, "Redis execute Lua script failed.");

        Long currentCount = result.get(0);
        Long remainingCount = result.get(1);

        log.info("rate limit key [{}] result: currentCount [{}], remainingCount [{}].", resolve, currentCount, remainingCount);

        return remainingCount > 0;
    }
}