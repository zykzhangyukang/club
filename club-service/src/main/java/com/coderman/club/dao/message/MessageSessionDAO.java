package com.coderman.club.dao.message;

import com.coderman.club.dao.common.BaseDAO;
import com.coderman.club.model.message.MessageSessionExample;
import com.coderman.club.model.message.MessageSessionModel;
import com.coderman.club.vo.message.MessageSessionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Administrator
 */
public interface MessageSessionDAO extends BaseDAO<MessageSessionModel, MessageSessionExample> {

    /**
     * 查询会话
     * @param userOne
     * @param userTwo
     * @return
     */
    MessageSessionModel selectSessionByUser(@Param(value = "userOne") Long userOne,@Param(value = "userTwo") Long userTwo);

    /**
     * 新增会话
     * @param messageSessionModel
     */
    void insertSelectiveReturnKey(MessageSessionModel messageSessionModel);

    /**
     * 获取用户会话
     * @param userId
     * @return
     */
    List<MessageSessionVO> selectSessionList(@Param(value = "userId") Long userId);
}