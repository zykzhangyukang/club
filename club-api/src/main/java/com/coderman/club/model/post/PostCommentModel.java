package com.coderman.club.model.post;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This is the base record class for table: club_post_comment
 * Generated by MyBatis Generator.
 * @author MyBatis Generator
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value="PostCommentModel", description = "club_post_comment 实体类")
public class PostCommentModel extends BaseModel {
    

    @ApiModelProperty(value = "主键id")
    private Long commentId;

    @ApiModelProperty(value = "帖子id")
    private Long postId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "创建时间")
    private Date createdAt;

    @ApiModelProperty(value = "是否启动")
    private Boolean isActive;
}