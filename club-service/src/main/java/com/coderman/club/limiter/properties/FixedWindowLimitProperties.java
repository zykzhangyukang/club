package com.coderman.club.limiter.properties;

import java.util.concurrent.TimeUnit;

/**
 * @author ：zhangyukang
 * @date ：2024/06/04 14:02
 */
public class FixedWindowLimitProperties implements LimitProperties {

    /**
     * 窗口大小
     */
    private final int windowSize;

    /**
     * 窗口请求
     */
    private final int windowRequests;

    /**
     * 限流时间维度，默认为秒
     * 支持秒，分钟，小时，天
     * 即，
     * {@link TimeUnit#SECONDS},
     * {@link TimeUnit#MINUTES},
     * {@link TimeUnit#HOURS},
     * {@link TimeUnit#DAYS}
     *
     * @return TimeUnit
     * @since 1.0.2
     */
    private TimeUnit timeUnit;


    public FixedWindowLimitProperties(int windowSize, int windowRequests, TimeUnit timeUnit) {
        this.windowSize = windowSize;
        this.windowRequests = windowRequests;
        this.timeUnit = timeUnit;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public int getWindowRequests() {
        return windowRequests;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }
}
