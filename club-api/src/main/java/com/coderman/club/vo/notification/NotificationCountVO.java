package com.coderman.club.vo.notification;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NotificationCountVO extends BaseModel {

    @ApiModelProperty(value = "消息类型")
    private String type;

    @ApiModelProperty(value = "未读消息数量")
    private Long unReadCount;
}
