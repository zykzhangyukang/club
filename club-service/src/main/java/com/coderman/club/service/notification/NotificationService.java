package com.coderman.club.service.notification;

import com.coderman.club.dto.notification.NotifyMsgDTO;
import com.coderman.club.vo.common.ResultVO;

import java.util.Map;

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

    /**
     * 获取未读消息数
     * @return
     */
    ResultVO<Map<String,Object>> getUnReadCount();

}
