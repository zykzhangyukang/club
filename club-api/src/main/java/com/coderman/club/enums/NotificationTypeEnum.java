package com.coderman.club.enums;

/**
 * @author ：zhangyukang
 * @date ：2023/11/24 15:37
 */
public enum NotificationTypeEnum {

    /**
     * 注册欢迎
     */
    REGISTER_WELCOME("register_welcome", "你好：%s , 您加入了我们这个超级酷的社区！欢迎来到这个疯狂有趣的地方，我们这里有各种神奇的东西等着您发现！"),
    /**
     *
     */
    REGISTER_INIT_PWD("register_init_pwd", "%s , 您的初始密码为: %s  ，请您尽快修改密码！"),
    /**
     * 关注用户
     */
    FOLLOWING_USER("following_user", "%s 关注了你"),
    /**
     * 点赞帖子
     */
    LIKE_POST("like_post", "%s 点赞了你的帖子 %s"),
    /**
     * 收藏帖子
     */
    COLLECT_POST("collect_post", "%s 收藏了你的帖子 %s"),
    /**
     * 评论帖子
     */
    COMMENT("comment", "%s 评论了你的帖子 “%s”  ：%s "),
    /**
     * 回复评论
     */
    REPLY("reply", "%s 回复了你的评论 “%s”  ：%s "),
    /**
     * 回复评论
     */
    REPLY_AT("reply_at", "%s 回复并@了你的评论 “%s”  ：%s ");

    NotificationTypeEnum(String msgType, String template) {
        this.template = template;
        this.msgType = msgType;
    }

    public static NotificationTypeEnum getByMsgType(String msgType){
        for (NotificationTypeEnum value : NotificationTypeEnum.values()) {
            if(value.getMsgType().equals(msgType)){
                return value;
            }
        }
        return null;
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
