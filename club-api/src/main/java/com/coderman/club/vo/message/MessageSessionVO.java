package com.coderman.club.vo.message;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author ：zhangyukang
 * @date ：2023/11/30 14:30
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageSessionVO extends BaseModel {

    @ApiModelProperty(value = "会话id")
    private Long sessionId;

    @ApiModelProperty(value = "用户1")
    private Long userOne;

    @ApiModelProperty(value = "用户2")
    private Long userTwo;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "最后一条消息内容")
    private String lastMessage;

    @ApiModelProperty(value = "最后发送人id")
    private Long lastUserId;

    @ApiModelProperty(value = "最后一条私信时间")
    private Date lastMessageTime;
}
