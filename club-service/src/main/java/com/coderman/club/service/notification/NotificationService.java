package com.coderman.club.service.notification;

import com.coderman.club.dto.notification.NotificationDTO;
import com.coderman.club.dto.notification.NotifyMsgDTO;
import com.coderman.club.vo.common.PageVO;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.notification.NotificationCommentVO;
import com.coderman.club.vo.notification.NotificationCountVO;
import com.coderman.club.vo.notification.NotificationVO;

import java.util.List;

/**
 * @author zhangyukang
 */
public interface NotificationService {

    /**
     * 保存消息并推送给用户。
     *
     * @param notifyMsgDTO 包含待发送消息的数据传输对象。
     */
    public void send(NotifyMsgDTO notifyMsgDTO);

    /**
     * 获取未读消息数。
     *
     * @return 包含未读消息数的响应结果对象。
     */
    ResultVO<NotificationCountVO> getUnReadCount();

    /**
     * 获取消息列表。
     *
     * @param notificationDTO 包含筛选条件的消息数据传输对象。
     * @return 包含消息列表的响应结果对象。
     */
    ResultVO<PageVO<List<NotificationCommentVO>>> getPage(NotificationDTO notificationDTO);

    /**
     * 标记消息为已读。
     *
     * @param notificationId 消息的唯一标识符。
     * @return 表示标记操作结果的响应结果对象。
     */
    ResultVO<Void> read(Long notificationId);

    /**
     * 删除消息。
     *
     * @param notificationId 消息的唯一标识符。
     * @return 表示删除操作结果的响应结果对象。
     */
    ResultVO<Void> delete(Long notificationId);
}
