package com.coderman.club.constant.redis;

/**
 * @author Administrator
 */
public interface RedisKeyConstant {

    /**
     * 全局分布式锁
     */
    public static final String REDIS_LOCK_PREFIX = "REDIS_LOCK:";
    /**
     * 登录分布式锁
     */
    public static final String REDIS_LOGIN_LOCK_PREFIX = "REDIS_LOGIN_LOCK";
    public static final String REDIS_REFRESH_LOCK_PREFIX = "REDIS_LOGIN_REFRESH";

    /**
     * 用户令牌访问
     */
    public static final String USER_ACCESS_TOKEN_PREFIX = "club.user:access_token:";
    /**
     * 用户刷新访问
     */
    public static final String USER_REFRESH_TOKEN_PREFIX = "club.user:refresh_token:";
    /**
     * 登录验证码
     */
    public static final String USER_LOGIN_CAPTCHA_PREFIX  = "club.login:captcha_code:";
}
