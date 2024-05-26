package com.coderman.club.vo.post;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostReplyVO extends BaseModel {


    @ApiModelProperty(value = "主键id")
    private Long commentId;

    @ApiModelProperty(value = "帖子id")
    private Long postId;

    @ApiModelProperty(value = "父级评论id")
    private Long parentId;

    @ApiModelProperty(value = "被回复的评论id")
    private Long replyId;

    @ApiModelProperty(value = "被评论昵称")
    private String toUserNickName;

    @ApiModelProperty(value = "被评论人头像")
    private String toUserAvatar;

    @ApiModelProperty(value = "被评论人id")
    private Long toUserId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "地址")
    private String location;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "点赞数")
    private Integer likes = 0;

    @ApiModelProperty(value = "回复数")
    private Integer replyCount;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
