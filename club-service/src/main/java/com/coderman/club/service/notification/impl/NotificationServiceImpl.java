package com.coderman.club.service.notification.impl;

import com.alibaba.fastjson.JSON;
import com.coderman.club.annotation.RedisChannelListener;
import com.coderman.club.constant.common.CommonConstant;
import com.coderman.club.constant.common.ResultConstant;
import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.dao.notification.NotificationDAO;
import com.coderman.club.dto.notification.NotifyMsgDTO;
import com.coderman.club.enums.NotificationTypeEnum;
import com.coderman.club.model.notification.NotificationModel;
import com.coderman.club.service.notification.NotificationService;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.utils.AuthUtil;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.notification.NotificationCountVO;
import com.coderman.club.vo.user.AuthUserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import com.coderman.club.websocket.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author ：zhangyukang
 * @date ：2023/11/24 15:30
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Resource
    private NotificationDAO notificationDAO;

    @Resource
    private SimpUserRegistry simpUserRegistry;

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    @Resource
    private RedisService redisService;


    @Override
    public void saveAndNotify(NotifyMsgDTO notifyMsgDTO) {

        Long senderId = notifyMsgDTO.getSenderId();
        List<Long> userIdList = notifyMsgDTO.getUserIdList();
        NotificationTypeEnum typeEnum = notifyMsgDTO.getTypeEnum();
        String content = notifyMsgDTO.getContent();
        String link = notifyMsgDTO.getLink();

        if (senderId == null || CollectionUtils.isEmpty(userIdList) || typeEnum == null) {

            throw new IllegalArgumentException("参数错误！");
        }
        if (StringUtils.isBlank(content) || StringUtils.length(content) > CommonConstant.LENGTH_256) {
            throw new IllegalArgumentException("发送的内容不能为空，且不超过256个字符！");
        }
        if (StringUtils.length(link) > CommonConstant.LENGTH_256) {
            throw new IllegalArgumentException("跳转链接不超过256个字符！");
        }

        for (Long userId : userIdList) {

            NotificationModel notificationModel = new NotificationModel();
            notificationModel.setSenderId(senderId);
            notificationModel.setContent(content);
            notificationModel.setCreateTime(new Date());
            notificationModel.setIsRead(Boolean.FALSE);
            notificationModel.setLink(link);
            notificationModel.setType(typeEnum.getMsgType());
            notificationModel.setUserId(userId);
            this.notificationDAO.insertSelective(notificationModel);

            // websocket推送
            this.sendToUser(senderId, userId, notificationModel);
        }

    }

    @Override
    public ResultVO<Map<String,Object>> getUnReadCount() {

        AuthUserVO current = AuthUtil.getCurrent();
        if(current == null){
            return ResultUtil.getWarn("用户未登录！");
        }

        List<NotificationCountVO> notificationCountVos =  this.notificationDAO.getUnReadCount(current.getUserId());
        long totalCount = 0L;
        for (NotificationCountVO notificationCountVo : notificationCountVos) {

            Long count = Optional.ofNullable(notificationCountVo.getUnReadCount()).orElse(0L);
            totalCount +=count;
        }

        Map<String,Object> map = new HashMap<>();
        map.put("totalUnReadCount", totalCount);
        map.put("notificationList", notificationCountVos);

        ResultVO<Map<String, Object>> resultVO = new ResultVO<>();
        resultVO.setCode(ResultConstant.RESULT_CODE_200);
        resultVO.setResult(map);
        return resultVO;
    }

    /**
     * 订阅redis主题，解决分布式多节点websocket连接问题。
     *
     * @param websocketRedisMsg 消息内容
     */
    @RedisChannelListener(channelName = RedisKeyConstant.CHANNEL_WEBSOCKET_NOTIFY, clazz = WebsocketRedisMsg.class)
    public void handWebSocketNotify(WebsocketRedisMsg<Object> websocketRedisMsg) {

        log.info("Received Message: {}", JSON.toJSONString(websocketRedisMsg));
        String receiver = websocketRedisMsg.getReceiver();
        Object content = websocketRedisMsg.getContent();
        String destination = websocketRedisMsg.getDestination();

        // 取出用户名并判断是否连接到当前应用节点的WebSocket
        SimpUser simpUser = simpUserRegistry.getUser(receiver);
        if (simpUser != null && StringUtils.isNoneBlank(simpUser.getName()) && StringUtils.isNotBlank(destination)) {

            //  给WebSocket客户端发送消息
            simpMessagingTemplate.convertAndSend(destination, content);
            log.info("handWebSocketNotify-websocket推送消息 destination => {} ,payload => {}", destination, JSON.toJSONString(content));
        }
    }

    private void sendToUser(Long senderId, Long receiverId, Object payload) {

        String subscribeUrl = WebSocketChannelEnum.USER_SYS_MSG.getSubscribeUrl();

        String sender = String.valueOf(senderId);
        String receiver = String.valueOf(receiverId);
        String destination = String.format(subscribeUrl, receiverId);
        SimpUser simpUser = simpUserRegistry.getUser(receiver);

        //如果接收者存在，则发送消息
        if (simpUser != null && StringUtils.isNoneBlank(simpUser.getName())) {

            simpMessagingTemplate.convertAndSend(destination, payload);
            log.info("sendToUser-websocket推送消息 destination => {} ,payload => {}", destination, JSON.toJSONString(payload));
        }
        //如果接收者在线，则说明接收者连接了集群的其他节点，需要通知接收者连接的那个节点发送消息
        else if (redisService.isSetMember(RedisKeyConstant.WEBSOCKET_USER_SET, receiver, RedisDbConstant.REDIS_DB_DEFAULT)) {

            WebsocketRedisMsg<Object> websocketRedisMsg = new WebsocketRedisMsg<>(receiver, destination, payload);
            redisService.sendMessage(RedisKeyConstant.CHANNEL_WEBSOCKET_NOTIFY, websocketRedisMsg);
        } else {

            log.info("消息接收者:{}还未建立WebSocket连接 [离线]，{} 发送的消息: {}", receiver, sender, payload);
        }
    }
}
