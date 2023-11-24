package com.coderman.club.websocket;

/**
 * Redis中存储WebSocket消息
 *
 * @author zifangsky
 * @date 2018/10/16
 * @since 1.0.0
 */
public class WebsocketRedisMsg<T> {
    /**
     * 消息接收者的username
     */
    private String receiver;
    /**
     * 消息对应的订阅频道的CODE，参考{@link WebSocketChannelEnum}的code字段
     */
    private String destination;
    /**
     * 消息正文
     */
    private T content;

    public WebsocketRedisMsg() {
    }

    public WebsocketRedisMsg(String receiver, String destination, T content) {
        this.receiver = receiver;
        this.destination = destination;
        this.content = content;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "RedisWebsocketMsg{" +
                "receiver='" + receiver + '\'' +
                ", channelCode='" + destination + '\'' +
                ", content=" + content +
                '}';
    }
}
