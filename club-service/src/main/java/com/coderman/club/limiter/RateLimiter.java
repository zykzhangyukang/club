package com.coderman.club.limiter;

import com.coderman.club.limiter.properties.LimitProperties;

/**
 * @author 限流器
 */
public interface RateLimiter {

    /**
     * 是否允许请求
     * @param key
     * @param properties
     * @return
     */
    boolean allowRequest(String key, LimitProperties properties);
}
