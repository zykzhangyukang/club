package com.coderman.club.limiter.properties;

import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * @date 2020/11/15 5:53 下午 星期日
 * @since 1.0.0
 */
@NoArgsConstructor
public class DefaultLimitProperties implements LimitProperties {

    private  int replenishRate;

    private  int burstCapacity;

    private TimeUnit timeUnit;

    public DefaultLimitProperties(int replenishRate, int burstCapacity, TimeUnit timeUnit) {
        this.replenishRate = replenishRate;
        this.burstCapacity = burstCapacity;
        this.timeUnit = timeUnit;
    }

    @Override
    public int replenishRate() {
        return replenishRate;
    }

    @Override
    public int burstCapacity() {
        return burstCapacity;
    }

    @Override
    public TimeUnit timeUnit() {
        return timeUnit;
    }
}
