package com.coderman.club.limiter;

/**
 * 限流策略
 * @author zhangyukang
 */
public interface LimiterStrategy {

    /**
     * 限流策略，默认为令牌桶算法
     */
    String TOKEN_BUCKET = "TOKEN_BUCKET";

    /**
     * 固定窗口限流
     */
    String FIXED_WINDOW = "FIXED_WINDOW";

    /**
     * 固定窗口限流
     */
    String SLIDING_WINDOW = "SLIDING_WINDOW";

}
