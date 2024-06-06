package com.coderman.club.vo.notification;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhangyukang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NotificationCommentVO extends BaseModel {

    @ApiModelProperty(value = "主键id")
    private Long commentId;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "评论是否已删除")
    private Boolean isHide;

    @ApiModelProperty(value = "父级评论是否已删除")
    private Boolean parentIsHide;

    @ApiModelProperty(value = "被回复的评论是否已删除")
    private Boolean repliedIsHide;

    @ApiModelProperty(value = "父级评论id")
    private Long parentId;

    @ApiModelProperty(value = "被回复的评论id")
    private Long replyId;

    @ApiModelProperty(value = "类型（评论：comment, 回复：reply）")
    private String type;

    @ApiModelProperty(value = "帖子id")
    private Long postId;

    @ApiModelProperty(value = "帖子标题")
    private String postTitle;

    @ApiModelProperty(value = "本评论的发布人")
    private String user;

    @ApiModelProperty(value = "本评论的发布人头像")
    private String avatar;

    @ApiModelProperty(value = "本评论的回复的人")
    private String toUser;

    @ApiModelProperty(value = "父级评论用户")
    private String parentUser;

    @ApiModelProperty(value = "被回复的评论内容的用户 (只有@评论才有)")
    private String repliedUser;

    @ApiModelProperty(value = "被回复的评论的类型 (只有@评论才有)")
    private String repliedType;

    @ApiModelProperty(value = "被回复的评论内容的用户 (被@的人)")
    private String toRepliedUser;

    @ApiModelProperty(value = "父级评论内容")
    private String parentContent;

    @ApiModelProperty(value = "被回复的评论内容")
    private String repliedContent;
}
