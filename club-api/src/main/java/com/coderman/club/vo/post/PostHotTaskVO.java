package com.coderman.club.vo.post;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ：zhangyukang
 * @date ：2023/12/08 12:22
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostHotTaskVO extends BaseModel{

    @ApiModelProperty(value = "任务批次编号")
    private Integer groupFlag;

    @ApiModelProperty(value = "任务开始id")
    private Long beginId;

    @ApiModelProperty(value = "任务结束id")
    private Long endId;

}
