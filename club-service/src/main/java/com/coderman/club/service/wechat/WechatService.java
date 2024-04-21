package com.coderman.club.service.wechat;

import com.coderman.club.dto.wechat.WxBaseMessageDTO;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.user.UserLoginVO;

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
    String replyMessage(WxBaseMessageDTO wxBaseMessageDTO);

    /**
     * 监听微信事件码输入
     *
     * @param deviceId
     * @return
     */
    ResultVO<UserLoginVO> subscribe(String deviceId);
}
