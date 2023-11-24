package com.coderman.club.websocket;

/**
 * @author Administrator
 */
public enum WebSocketChannelEnum {

    /**
     * 用户系统消息
     */
    USER_SYS_MSG("USER_SYS_MSG", "用户系统消息", "/user/%d/sysMsg"),
    /**
     * 系统广播消息
     */
    TOPIC_SYS_MSG("TOPIC_SYS_MSG", "系统广播消息", "/topic/sysMsg");


    WebSocketChannelEnum(String code, String description, String subscribeUrl) {
        this.description = description;
        this.subscribeUrl = subscribeUrl;
    }

    /**
     * 描述
     */
    private String description;
    /**
     * WebSocket客户端订阅的URL
     */
    private String subscribeUrl;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubscribeUrl() {
        return subscribeUrl;
    }

    public void setSubscribeUrl(String subscribeUrl) {
        this.subscribeUrl = subscribeUrl;
    }
}