package com.coderman.club.model.post;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * This is the base record class for table: club_post_comment
 * Generated by MyBatis Generator.
 * @author coderman - MyBatis Generator
 */
@Data
@ApiModel(value="PostCommentModel", description = "club_post_comment 实体类")
@TableName(value="club_post_comment")
public class PostCommentModel implements Serializable {
    
    @TableId(value = "comment_id",type = IdType.AUTO)
    @ApiModelProperty(value = "主键id")
    private Long commentId;

    @ApiModelProperty(value = "帖子id")
    private Long postId;

    @ApiModelProperty(value = "父级评论id")
    private Long parentId;

    @ApiModelProperty(value = "被回复的评论id")
    private Long replyId;

    @ApiModelProperty(value = "子评论的数量")
    private Integer replyCount;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "被评论人id")
    private Long toUserId;

    @ApiModelProperty(value = "地址")
    private String location;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "类型（评论：comment, 回复：reply）")
    private String type;

    @ApiModelProperty(value = "评论内容")
    private String content;
}