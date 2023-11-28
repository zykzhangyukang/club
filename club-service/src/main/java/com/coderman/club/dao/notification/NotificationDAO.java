package com.coderman.club.dao.notification;

import com.coderman.club.dao.common.BaseDAO;
import com.coderman.club.model.notification.NotificationExample;
import com.coderman.club.model.notification.NotificationModel;
import com.coderman.club.vo.notification.NotificationCountVO;
import com.coderman.club.vo.notification.NotificationVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author Administrator
 */
public interface NotificationDAO extends BaseDAO<NotificationModel, NotificationExample> {

    /**
     * 获取未读信息数量
     * @param userId
     * @return
     */
    List<NotificationCountVO> getUnReadCount(@Param(value = "userId") Long userId);

    /**
     * 获取消息列表
     *
     * @param userId
     * @param isRead
     * @param type
     * @return
     */
    List<NotificationVO> getList(@Param(value = "userId") Long userId,@Param(value = "isRead") Boolean isRead,@Param(value = "type") String type);

    /**
     * 修改消息状态
     *
     * @param isRead
     * @param userId
     * @param notificationId
     * @return
     */
    int updateReadStatus(@Param(value = "isRead") Integer isRead,@Param(value = "userId") Long userId,@Param(value = "notificationId") Long notificationId);
}