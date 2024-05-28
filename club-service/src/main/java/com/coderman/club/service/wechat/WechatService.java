package com.coderman.club.service.wechat;

import com.coderman.club.dto.wechat.WxBaseMessageDTO;
import com.coderman.club.vo.common.ResultVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @author zhangyukang
 */
public interface WechatService {

    /**
     * 获取公众号事件码
     *
     * @param deviceId 设备id
     * @return
     */
    ResultVO<String> getEventCode(String deviceId);

    /**
     *  接受文本消息
     * @param wxBaseMessageDTO
     * @return
     */
    String replyMessage(WxBaseMessageDTO wxBaseMessageDTO) throws IOException;

    /**
     * 监听微信事件码输入
     *
     * @param deviceId
     * @return
     */
    SseEmitter subscribe(String deviceId);
}
