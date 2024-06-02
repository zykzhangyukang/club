package com.coderman.club.dto.notification;

import com.coderman.club.enums.NotificationTypeEnum;
import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author ：zhangyukang
 * @date ：2023/11/24 15:34
 */
@Builder
@EqualsAndHashCode(callSuper = true)
@Data
public class NotifyMsgDTO extends BaseModel {

    @ApiModelProperty(value = "通知的具体内容")
    private String content;

    @ApiModelProperty(value = "关联业务id")
    private Long relationId;

    @ApiModelProperty(value = "消息类型")
    private NotificationTypeEnum typeEnum;

    @ApiModelProperty(value = "接收通知的用户的唯一标识符")
    private List<Long> userIdList;

    @ApiModelProperty(value = "发送通知的用户或系统的唯一标识符")
    private Long senderId;
}
