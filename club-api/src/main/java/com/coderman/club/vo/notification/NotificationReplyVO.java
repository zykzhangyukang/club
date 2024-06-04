package com.coderman.club.vo.notification;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NotificationReplyVO extends BaseModel {

    @ApiModelProperty(value = "id")
    private Long commentId;

    @ApiModelProperty(value = "回复内容")
    private String content;

    @ApiModelProperty(value = "发送人昵称")
    private String sendNickName;

    @ApiModelProperty(value = "接收人昵称")
    private String toNickName;

    @ApiModelProperty(value = "类型")
    private String type;
}
