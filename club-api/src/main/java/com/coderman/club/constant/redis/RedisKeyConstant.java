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
     * 业务分布式锁
     */
    public static final String REDIS_LOGIN_LOCK_PREFIX = "REDIS_LOGIN_LOCK:";
    public static final String REDIS_REGISTER_LOCK_PREFIX = "REDIS_REGISTER_LOCK:";
    public static final String REDIS_REFRESH_LOCK_PREFIX = "REDIS_LOGIN_REFRESH_LOCK:";
    public static final String REDIS_FOLLOW_LOCK_PREFIX = "REDIS_FOLLOW_LOCK:";
    public static final String REDIS_UNFOLLOW_LOCK_PREFIX = "REDIS_UNFOLLOW_LOCK:";
    public static final String REDIS_POST_CREATE_LOCK_PREFIX = "REDIS_POST_CREATE_LOCK:";

    /**
     * 防重token
     */
    public static final String REDIS_POST_REPEAT = "REDIS_POST_REPEAT:";

    /**
     * 论坛板块缓存
     */
    public static final String REDIS_SECTION_CACHE = "REDIS_SECTION_CACHE";
    public static final String REDIS_CAROUSE_CACHE = "REDIS_CAROUSE_CACHE";

    /**
     * websocket回话
     */
    public static final String WEBSOCKET_USER_SET = "websocket:user_set";
    /**
     * websocket消息广播
     */
    public static final String CHANNEL_WEBSOCKET_NOTIFY = "TOPIC://WEBSOCKET_NOTIFY";


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
    public static final String USER_LOGIN_CAPTCHA_PREFIX = "club.login:captcha_code:";
    public static final String USER_REGISTER_CAPTCHA_PREFIX = "club.register:captcha_code:";
    /**
     * 帖子浏览量防刷
     */
    public static final String POST_VIEWS_LIMIT_PREFIX = "club:post:views_limit:";
}
