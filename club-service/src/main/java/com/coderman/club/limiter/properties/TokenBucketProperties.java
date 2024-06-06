package com.coderman.club.limiter.properties;

import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangyukang
 * @date 2020/11/15 5:53 下午 星期日
 * @since 1.0.0
 */
@NoArgsConstructor
public class TokenBucketProperties implements LimitProperties {

    /**
     * 令牌桶每秒填充平均速率
     *
     * @return replenishRate
     */
    private  int replenishRate;

    /**
     * 令牌桶总容量
     *
     * @return burstCapacity
     */
    private  int burstCapacity;


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

    public TokenBucketProperties(int replenishRate, int burstCapacity, TimeUnit timeUnit) {
        this.replenishRate = replenishRate;
        this.burstCapacity = burstCapacity;
        this.timeUnit = timeUnit;
    }

    public int getReplenishRate() {
        return replenishRate;
    }

    public void setReplenishRate(int replenishRate) {
        this.replenishRate = replenishRate;
    }

    public int getBurstCapacity() {
        return burstCapacity;
    }

    public void setBurstCapacity(int burstCapacity) {
        this.burstCapacity = burstCapacity;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }
}
