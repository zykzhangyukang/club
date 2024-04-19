package com.coderman.club.dto.wechat;

import com.coderman.club.model.common.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ：zhangyukang
 * @date ：2024/04/19 16:09
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class WxBaseMessageDTO extends BaseModel {

    /**
     * 接收方帐号（收到的OpenID）
     */
    @JacksonXmlProperty(localName = "ToUserName")
    private String toUserName;

    /**
     * 开发者微信号
     */
    @JacksonXmlProperty(localName = "FromUserName")
    private String fromUserName;

    /**
     * 消息创建时间 （整型）
     */
    @JacksonXmlProperty(localName = "CreateTime")
    private Long createTime;

    /**
     * 消息类型
     */
    @JacksonXmlProperty(localName = "MsgType")
    private String msgType;

    /**
     * 消息内容
     */
    @JacksonXmlProperty(localName = "Content")
    private String content;

    /**
     * 消息ID
     */
    @JacksonXmlProperty(localName = "MsgId")
    private String msgId;
}
