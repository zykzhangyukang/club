package com.coderman.club.service.notification;

import com.coderman.club.dto.notification.NotifyMsgDTO;

/**
 * @author Administrator
 */
public interface NotificationService {

    /**
     * 保存消息并推送给用户
     *
     * @param notifyMsgDTO
     */
    public void saveAndNotify(NotifyMsgDTO notifyMsgDTO);
}
