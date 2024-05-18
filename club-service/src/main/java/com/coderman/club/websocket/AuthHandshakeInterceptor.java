package com.coderman.club.websocket;

import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.vo.user.AuthUserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 建立连接时候的权限拦截器
 * @author zhangyukang
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Lazy(value = false)
public class AuthHandshakeInterceptor implements ChannelInterceptor {

    @Resource
    private RedisService redisService;

    @Override
    @SuppressWarnings("all")
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        // 不是第一次连接
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {

            return message;
        }

        List<String> nativeHeader = accessor.getNativeHeader(HttpHeaders.AUTHORIZATION);
        String sessionId = accessor.getSessionId();
        if (CollectionUtils.isEmpty(nativeHeader)) {

            log.error("未登录系统，禁止连接WebSocket!,sessionId:{}",sessionId);
            return null;
        }

        String token = nativeHeader.get(0);
        AuthUserVO userVO = this.redisService.getObject(RedisKeyConstant.USER_ACCESS_TOKEN_PREFIX + token, AuthUserVO.class, RedisDbConstant.REDIS_DB_DEFAULT);
        if (userVO == null) {

            log.error("未登录系统，禁止连接WebSocket! ,sessionId:{}", sessionId);
            return null;
        }

        Long userId = userVO.getUserId();

        // 单节点会话
        accessor.setUser(new MyPrincipal(userId));
        if (this.redisService.isSetMember(RedisKeyConstant.WEBSOCKET_USER_SET, String.valueOf(userId), RedisDbConstant.REDIS_DB_DEFAULT)) {

            log.debug("同一个用户:{} 不准建立多个连接WebSocket. sessionId:{}", userId, sessionId);
            return null;
        }

        // 将用户id存到Redis中
        this.redisService.addToSet(RedisKeyConstant.WEBSOCKET_USER_SET, String.valueOf(userId), RedisDbConstant.REDIS_DB_DEFAULT);
        log.info("用户:{} 请求建立WebSocket连接, sessionId:{}", userId, sessionId);

        return message;
    }

}
