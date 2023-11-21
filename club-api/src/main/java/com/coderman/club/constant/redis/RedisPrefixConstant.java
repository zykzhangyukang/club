package com.coderman.club.constant.redis;

public interface RedisPrefixConstant {

    /**
     * 分布式锁
     */
    public static final String REDIS_LOCK_PREFIX = "REDIS_LOCK:";

    /**
     * 用户令牌访问
     */
    public static final String USER_ACCESS_TOKEN_PREFIX  = "club.user.token:";
}
