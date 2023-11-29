package com.coderman.club.dao.message;

import com.coderman.club.dao.common.BaseDAO;
import com.coderman.club.model.message.MessageSessionRelationExample;
import com.coderman.club.model.message.MessageSessionRelationModel;
import org.apache.ibatis.annotations.Param;

/**
 * @author Administrator
 */
public interface MessageSessionRelationDAO extends BaseDAO<MessageSessionRelationModel, MessageSessionRelationExample> {

    /**
     *查询用户会话关联
     * @param userId
     * @param sessionId
     * @return
     */
    MessageSessionRelationModel selectUserRelation(@Param(value = "userId") Long userId,@Param(value = "sessionId") Long sessionId);

    /**
     * 清空会话未读数
     * @param targetUserId
     * @param sessionId
     */
    void updateReadCountZero(@Param(value = "userId") Long targetUserId,@Param(value = "sessionId") Long sessionId);
}