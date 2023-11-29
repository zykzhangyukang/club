package com.coderman.club.service.message;

import com.coderman.club.dto.message.MessageSendDTO;
import com.coderman.club.vo.common.ResultVO;

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
}
