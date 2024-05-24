package com.coderman.club.vo.post;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ：zhangyukang
 * @date ：2023/12/08 12:22
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostHotTaskVO extends BaseModel implements Delayed {

    @ApiModelProperty(value = "任务批次编号")
    private Integer groupFlag;

    @ApiModelProperty(value = "任务开始id")
    private Long beginId;

    @ApiModelProperty(value = "任务结束id")
    private Long endId;

    @ApiModelProperty(value = "延时处理时间")
    private Long delayTime;

    private final AtomicInteger retry =  new AtomicInteger(0);

    public void setDelayTime(long delayTime) {
        this.delayTime = System.nanoTime() + TimeUnit.SECONDS.toNanos(delayTime);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.delayTime  - System.nanoTime(),TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed delayed) {
        return (int) (this.getDelay(TimeUnit.SECONDS) - delayed.getDelay(TimeUnit.SECONDS));
    }
}
