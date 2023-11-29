package com.coderman.club.dto.message;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ：zhangyukang
 * @date ：2023/11/29 12:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageSendDTO extends BaseModel {

    @ApiModelProperty(value = "接受人id")
    private Long receiverId;

    @ApiModelProperty(value = "消息内容")
    private String content;
}
