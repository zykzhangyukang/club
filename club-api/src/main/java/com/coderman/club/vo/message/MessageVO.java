package com.coderman.club.vo.message;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author ：zhangyukang
 * @date ：2023/11/29 15:15
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageVO extends BaseModel {

    @ApiModelProperty(value = "消息id")
    private Long messageId;

    @ApiModelProperty(value = "回话id")
    private Long sessionId;

    @ApiModelProperty(value = "发送人id")
    private Long senderId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "私信内容")
    private String content;

}
