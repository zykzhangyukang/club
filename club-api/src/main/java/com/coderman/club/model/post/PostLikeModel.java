package com.coderman.club.model.post;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This is the base record class for table: club_post_like
 * Generated by MyBatis Generator.
 * @author MyBatis Generator
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value="PostLikeModel", description = "club_post_like 实体类")
public class PostLikeModel extends BaseModel {
    

    @ApiModelProperty(value = "主键id")
    private Long postLikeId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "帖子id")
    private Long postId;

    @ApiModelProperty(value = "点赞状态")
    private String status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}