package com.coderman.club.vo.notification;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhangyukang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NotificationCountVO extends BaseModel {

    @ApiModelProperty(value = "全部消息")
    private Integer totalCount;

    @ApiModelProperty(value = "系统消息")
    private Integer sysCount;

    @ApiModelProperty(value = "点赞我的")
    private Integer zanCount;

    @ApiModelProperty(value = "回复我的")
    private Integer replyCount;

    @ApiModelProperty(value = "我的私信")
    private Integer chatCount;
}
