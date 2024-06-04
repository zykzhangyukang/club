package com.coderman.club.limiter.strategy;

import com.coderman.club.limiter.RateLimiter;
import com.coderman.club.limiter.properties.FixedWindowLimitProperties;
import com.coderman.club.limiter.properties.LimitProperties;
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
 * 固定时间窗口限流器实现
 *
 * @author zhangyukang
 * @date 2024/06/04 12:18
 */
@Component
@Slf4j
public class FixedWindowRateLimiter implements RateLimiter {

    @Resource
    private RedisService redisService;

    @Override
    public boolean allowRequest(String resolve, LimitProperties properties) {
        // 获取固定时间窗口的配置信息
        FixedWindowLimitProperties windowProperties = (FixedWindowLimitProperties) properties;

        // 根据限流时间维度计算时间
        long timeLong = getCurrentTimeLong(windowProperties.getTimeUnit());
        int windowSize = windowProperties.getWindowSize();
        int windowRequests = windowProperties.getWindowRequests();

        // The arguments to the LUA script. time() returns unixTime in seconds.
        List<String> scriptArgs = Arrays.asList(
                windowRequests + "",
                (timeLong * windowSize)  + ""
        );

        // 执行 Lua 脚本查询当前窗口内的请求数量
        List<Long> response = redisService.executeLuaScript(Long.class, "lua/FixedWindowRate.lua", Collections.singletonList(resolve), scriptArgs.toArray());
        Long currentCount = response.get(0);
        Long limitCount = response.get(1);

        // 第一个参数是是否允许请求，第二个参数是当前窗口内的请求数量
        Assert.notNull(response, "Redis execute Lua script failed.");

        log.info("rate limit key [{}] result: currentCount [{}] limitCount [{}].", resolve, currentCount, limitCount);

        return currentCount > 0;
    }


    // 获取当前时间对应的秒数
    private long getCurrentTimeLong(TimeUnit timeUnit) {
        switch (timeUnit) {
            case SECONDS:
                return 1;
            case MINUTES:
                return 60;
            case HOURS:
                return 3600;
            case DAYS:
                return 86400;
            default:
                throw new IllegalArgumentException("Time unit " + timeUnit + " is not supported.");
        }
    }
}
