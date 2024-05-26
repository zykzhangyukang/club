package com.coderman.club.dto.post;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostCommentDTO extends BaseModel {

    @ApiModelProperty(value = "帖子id")
    private Long postId;

    @ApiModelProperty(value = "父级评论id")
    private Long parentId;

    @ApiModelProperty(value = "评论内容")
    private String content;

}
