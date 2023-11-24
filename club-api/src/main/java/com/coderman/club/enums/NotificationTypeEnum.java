package com.coderman.club.enums;

/**
 * @author ：zhangyukang
 * @date ：2023/11/24 15:37
 */
public enum NotificationTypeEnum {

    /**
     * 注册欢迎
     */
    REGISTER_WELCOME("register_welcome", "喂 ！【%s】哇哦，您加入了我们这个超级酷的社区！欢迎来到这个疯狂有趣的地方，我们这里有各种神奇的东西等着您发现！"),
    ;

    NotificationTypeEnum(String msgType, String template) {
        this.template = template;
        this.msgType = msgType;
    }

    private final String template;
    private final String msgType;

    public String getTemplate() {
        return template;
    }

    public String getMsgType() {
        return msgType;
    }

}
