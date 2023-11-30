package com.coderman.club.utils;

import com.alibaba.fastjson.JSON;
import com.coderman.club.annotation.RedisChannelListener;
import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.websocket.WebSocketChannelEnum;
import com.coderman.club.websocket.WebsocketRedisMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ：zhangyukang
 * @date ：2023/11/30 14:15
 */
@Component
@Slf4j
public class WebsocketUtil {

    @Resource
    private SimpUserRegistry simpUserRegistry;

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    @Resource
    private RedisService redisService;

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

    public void sendToUser(Long senderId, Long receiverId, Object payload) {

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
