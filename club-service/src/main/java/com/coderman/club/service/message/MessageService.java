package com.coderman.club.service.message;

import com.coderman.club.dto.message.MessageSendDTO;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.message.MessageVO;

import java.util.List;

/**
 * @author Administrator
 */
public interface MessageService {

    /**
     * 发送私信
     *
     * @param messageSendDTO
     * @return
     */
    ResultVO<Void> send(MessageSendDTO messageSendDTO);

    /**
     * 获取会话中的信息
     *
     * @param sessionId
     * @return
     */
    ResultVO<List<MessageVO>> getSessionMessages(Long sessionId);
}
