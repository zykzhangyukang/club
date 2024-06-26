package com.coderman.club.mapper.notification;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coderman.club.model.notification.NotificationModel;
import com.coderman.club.vo.notification.NotificationCommentVO;
import com.coderman.club.vo.notification.NotificationCountVO;
import com.coderman.club.vo.notification.NotificationVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * This is the base Mapper class for table: club_notification
 * Generated by MyBatis Generator.
 * @author coderman - MyBatis Generator
 */
public interface NotificationMapper extends BaseMapper<NotificationModel> {

    /**
     * 获取未读信息数量
     * @param userId
     * @return
     */
    NotificationCountVO getUnReadCount(@Param(value = "userId") Long userId);

    /**
     * 获取消息列表
     *
     * @param userId
     * @param isRead
     * @param type
     * @return
     */
    List<NotificationVO> getPage(@Param(value = "userId") Long userId, @Param(value = "isRead") Boolean isRead, @Param(value = "type") String type);

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