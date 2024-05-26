package com.coderman.club.vo.post;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostCommentVO extends BaseModel {

    @ApiModelProperty(value = "主键id")
    private Long commentId;

    @ApiModelProperty(value = "帖子id")
    private Long postId;

    @ApiModelProperty(value = "父级评论id")
    private Long parentId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "被评论人id")
    private Long toUserId;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "地址")
    private String location;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "点赞数")
    private Integer likes = 0;

    @ApiModelProperty(value = "回复列表")
    private List<PostReplyVO> replies;
}