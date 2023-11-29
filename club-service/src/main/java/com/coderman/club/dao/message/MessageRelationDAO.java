package com.coderman.club.dao.message;

import com.coderman.club.dao.common.BaseDAO;
import com.coderman.club.model.message.MessageRelationExample;
import com.coderman.club.model.message.MessageRelationModel;
import org.apache.ibatis.annotations.Param;

/**
 * @author Administrator
 */
public interface MessageRelationDAO extends BaseDAO<MessageRelationModel, MessageRelationExample> {

    /**
     * 更新消息为已读
     *
     * @param targetUserId
     * @param sessionId
     */
    void updateReadStatus(@Param(value = "userId") Long targetUserId, @Param(value = "sessionId") Long sessionId);
}