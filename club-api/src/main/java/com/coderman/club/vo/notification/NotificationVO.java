package com.coderman.club.vo.notification;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author ：zhangyukang
 * @date ：2023/11/28 9:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NotificationVO extends BaseModel {

    @ApiModelProperty(value = "主键")
    private Long notificationId;

    @ApiModelProperty(value = "发送通知的用户或系统的唯一标识符")
    private Long senderId;

    @ApiModelProperty(value = "接收通知的用户的唯一标识符")
    private Long userId;

    @ApiModelProperty(value = "关联业务id")
    private Long relationId;

    @ApiModelProperty(value = "通知的类型（例如：点赞、评论、回复、系统通知等）")
    private String type;

    @ApiModelProperty(value = "本评论的发布人")
    private String user;

    @ApiModelProperty(value = "本评论的回复的人")
    private String toUser;

    @ApiModelProperty(value = "通知的具体内容")
    private String content;

    @ApiModelProperty(value = "被回复的评论内容的用户 (只有@评论才有)")
    private String repliedUser;

    @ApiModelProperty(value = "被回复的评论内容的用户 (被@的人)")
    private String toRepliedUser;

    @ApiModelProperty(value = "被回复的内容 (只有@的情况下才有)")
    private String repliedContent;

    @ApiModelProperty(value = "父级评论内容")
    private String parentContent;

    @ApiModelProperty(value = "帖子标题")
    private String postTitle;

    @ApiModelProperty(value = "帖子id")
    private Long postId;

    @ApiModelProperty(value = "标记通知是否已被用户阅读")
    private Boolean isRead;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
