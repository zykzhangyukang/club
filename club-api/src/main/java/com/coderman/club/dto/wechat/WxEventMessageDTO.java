package com.coderman.club.dto.wechat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订阅/取消订阅
 * @author Administrator
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WxEventMessageDTO extends WxBaseMessageDTO {

    /**
     * 事件类型
     */
    @JacksonXmlProperty(localName = "Event")
    private String event;

}
