package com.coderman.club.dao.notification;

import com.coderman.club.dao.common.BaseDAO;
import com.coderman.club.model.notification.NotificationExample;
import com.coderman.club.model.notification.NotificationModel;
import com.coderman.club.vo.notification.NotificationCountVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Administrator
 */
public interface NotificationDAO extends BaseDAO<NotificationModel, NotificationExample> {

    /**
     * 获取未读信息数量
     * @return
     */
    List<NotificationCountVO> getUnReadCount(@Param(value = "userId") Long userId);

}