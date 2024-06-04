package com.coderman.club.annotation;

import com.coderman.club.limiter.LimiterStrategy;
import com.coderman.club.limiter.properties.LimitProperties;
import com.coderman.club.limiter.properties.TokenBucketProperties;
import com.coderman.club.limiter.resolver.KeyResolver;
import com.coderman.club.limiter.resolver.UserKeyResolver;
import com.coderman.club.limiter.strategy.FixedWindowRateLimiter;
import com.coderman.club.limiter.strategy.TokenBucketRateLimiter;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解，用于标记需要进行限流控制的方法
 * 支持两种限流策略：令牌桶算法和固定时间窗口算法
 * 默认使用令牌桶算法
 *
 * @author zhangyukang
 * @see TokenBucketProperties 令牌桶算法的限流配置属性
 * @see FixedWindowRateLimiter 固定时间窗口限流器
 * @see TokenBucketRateLimiter 令牌桶限流器
 * @see com.coderman.club.limiter.RateLimiter 限流器接口
 * @see com.coderman.club.limiter.resolver.KeyResolver 限流维度解析器接口
 * @see com.coderman.club.limiter.resolver.IpKeyResolver 默认使用 IP 地址作为限流维度
 * @see LimitProperties 限流配置属性接口
 * @see java.util.concurrent.TimeUnit 限流时间维度单位枚举
 * @since 1.0.0
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 限流策略，默认为 "TOKEN_BUCKET"，可选值为 "TOKEN_BUCKET" 或 "FIXED_WINDOW"
     *
     * @return 限流策略
     */
    String strategy() default LimiterStrategy.TOKEN_BUCKET;

    /**
     * 限流维度解析器，默认使用 IpKeyResolver 解析 IP 地址作为限流维度
     *
     * @return 限流维度解析器类
     */
    Class<? extends KeyResolver> keyResolver() default UserKeyResolver.class;

    /** 固定窗口算法配置 **/

    /**
     * 窗口内允许的最大请求数量，默认为 5
     *
     * @return 窗口内允许的最大请求数量
     */
    int windowRequests() default 3;

    /**
     * 时间窗口大小，默认为 1 秒
     *
     * @return 时间窗口大小
     */
    int windowSize() default 1;


    /** 令牌桶算法配置 **/

    /**
     * 令牌桶每秒填充平均速率，默认为 1
     *
     * @return 令牌桶每秒填充平均速率
     */
    int replenishRate() default 1;

    /**
     * 令牌桶总容量，默认为 3
     *
     * @return 令牌桶总容量
     */
    int burstCapacity() default 3;

    /**
     * 限流时间维度，默认为秒
     * 支持秒、分钟、小时、天
     * 可选值为 TimeUnit.SECONDS、TimeUnit.MINUTES、TimeUnit.HOURS、TimeUnit.DAYS
     *
     * @return 限流时间维度单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
