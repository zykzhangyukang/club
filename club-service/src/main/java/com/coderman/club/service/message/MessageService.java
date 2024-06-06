package com.coderman.club.service.message;

import com.coderman.club.dto.message.MessageSendDTO;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.message.MessageSessionVO;
import com.coderman.club.vo.message.MessageVO;

import java.util.List;

/**
 * @author zhangyukang
 */
public interface MessageService {

    /**
     * 发送私信。
     *
     * @param messageSendDTO 包含私信内容和接收者信息的数据传输对象。
     * @return 表示发送操作结果的响应结果对象。
     */
    ResultVO<Void> send(MessageSendDTO messageSendDTO);

    /**
     * 获取指定会话中的所有私信消息。
     *
     * @param sessionId 会话的唯一标识符。
     * @return 包含会话中私信消息的响应结果对象。
     */
    ResultVO<List<MessageVO>> getSessionMessages(Long sessionId);

    /**
     * 将指定会话中的聊天消息标记为已读。
     *
     * @param targetUserId 会话的另一方用户ID。
     * @param sessionId    会话的唯一标识符。
     */
    void updateReadStatus(Long targetUserId, Long sessionId);

    /**
     * 获取当前用户的所有会话列表。
     *
     * @return 包含当前用户会话列表的响应结果对象。
     */
    ResultVO<List<MessageSessionVO>> getSessions();

    /**
     * 关闭指定会话。
     *
     * @param sessionId 待关闭会话的唯一标识符。
     * @return 表示关闭操作结果的响应结果对象。
     */
    ResultVO<Void> closeSession(Long sessionId);


}
