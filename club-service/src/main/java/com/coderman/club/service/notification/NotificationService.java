package com.coderman.club.service.notification;

import com.coderman.club.dto.notification.NotificationDTO;
import com.coderman.club.dto.notification.NotifyMsgDTO;
import com.coderman.club.vo.common.PageVO;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.notification.NotificationCountVO;
import com.coderman.club.vo.notification.NotificationVO;

import java.util.List;

/**
 * @author Administrator
 */
public interface NotificationService {

    /**
     * 保存消息并推送给用户
     *
     * @param notifyMsgDTO
     */
    public void send(NotifyMsgDTO notifyMsgDTO);

    /**
     * 获取未读消息数
     * @return
     */
    ResultVO<NotificationCountVO> getUnReadCount();

    /**
     * 获取消息列表
     * @return
     */
    ResultVO<PageVO<List<NotificationVO>>> getPage(NotificationDTO notificationDTO);

    /**
     * 已读消息
     *
     * @param notificationId
     * @return
     */
    ResultVO<Void> read(Long notificationId);
}
