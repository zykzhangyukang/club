package com.coderman.club.dto.wechat;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ：zhangyukang
 * @date ：2024/04/19 17:34
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WxBaseMessageDTO extends BaseModel {


    @ApiModelProperty(value = "接收方帐号（收到的OpenID）")
    private String toUserName;

    @ApiModelProperty(value = "开发者微信号")
    private String fromUserName;

    @ApiModelProperty(value = "消息创建时间 （整型）")
    private String createTime;

    @ApiModelProperty(value = "消息类型")
    private String msgType;

    @ApiModelProperty(value = "消息内容")
    private String content;

    @ApiModelProperty(value = "消息ID")
    private String msgId;

    @ApiModelProperty(value = "事件类型")
    private String event;


}
