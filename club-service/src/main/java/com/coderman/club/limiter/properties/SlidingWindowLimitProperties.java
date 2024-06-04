package com.coderman.club.limiter.properties;

import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author ：zhangyukang
 * @date ：2024/06/04 17:26
 */
@Data
public class SlidingWindowLimitProperties implements LimitProperties {
    /**
     * 窗口大小，单位为秒
     */
    private int windowSize;

    /**
     * 窗口内最大请求数
     */
    private int windowRequests;

    /**
     * 时间单位
     */
    private TimeUnit timeUnit;

    public SlidingWindowLimitProperties(int windowSize, int windowRequests, TimeUnit timeUnit) {
        this.windowSize = windowSize;
        this.windowRequests = windowRequests;
        this.timeUnit = timeUnit;
    }
}
