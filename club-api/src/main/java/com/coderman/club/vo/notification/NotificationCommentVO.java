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

    @ApiModelProperty(value = "父级评论id")
    private Long parentId;

    @ApiModelProperty(value = "被回复的评论id")
    private Long replyId;

    @ApiModelProperty(value = "类型（评论：comment, 回复：reply）")
    private String type;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "被回复的评论内容")
    private String repliedContent;
}
