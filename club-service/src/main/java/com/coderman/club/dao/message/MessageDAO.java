package com.coderman.club.dao.message;

import com.coderman.club.dao.common.BaseDAO;
import com.coderman.club.model.message.MessageExample;
import com.coderman.club.model.message.MessageModel;
import com.coderman.club.vo.message.MessageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Administrator
 */
public interface MessageDAO extends BaseDAO<MessageModel, MessageExample> {

    /**
     * 获取用户会话中的信息列表
     * @param userId
     * @param sessionId
     * @return
     */
    List<MessageVO> selectUserMessages(@Param(value = "userId") Long userId,@Param(value = "sessionId") Long sessionId);


    /**
     * 新增消息
     * @param messageModel
     * @return
     */
    int insertSelectiveReturnKey(MessageModel messageModel);
}