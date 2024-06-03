package com.coderman.club.vo.notification;

import com.coderman.club.model.post.PostCommentModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NotificationCommentVO extends PostCommentModel {

    @ApiModelProperty(value = "被回复的评论")
    private String replyContent;
}
