package com.coderman.club.dto.post;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostCommentPageDTO extends BaseModel {

    @ApiModelProperty(value = "当前页")
    private Integer currentPage;

    @ApiModelProperty(value = "每页显示大小")
    private Integer pageSize;

    @ApiModelProperty(value = "跳转id")
    private String seekCid;

    @ApiModelProperty(value = "帖子id")
    private Long postId;

    /**
     * service参数
     */

    @ApiModelProperty(value = "根评论排序")
    private List<Long> orderByComments;

    @ApiModelProperty(value = "回复排序")
    private List<Long> orderByReplies;

}
