package com.coderman.club.model.message;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This is the base record class for table: club_message_session
 * Generated by MyBatis Generator.
 * @author MyBatis Generator
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value="MessageSessionModel", description = "club_message_session 实体类")
public class MessageSessionModel extends BaseModel {
    

    @ApiModelProperty(value = "会话id")
    private Long sessionId;

    @ApiModelProperty(value = "用户1")
    private Long userOne;

    @ApiModelProperty(value = "用户2")
    private Long userTwo;

    @ApiModelProperty(value = "最后一条消息内容")
    private String lastMessage;

    @ApiModelProperty(value = "最后发送人id")
    private Long lastUserId;

    @ApiModelProperty(value = "最后一条私信时间")
    private Date lastMessageTime;
}