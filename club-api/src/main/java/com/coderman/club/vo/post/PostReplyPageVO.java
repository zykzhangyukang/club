package com.coderman.club.vo.post;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostReplyPageVO extends BaseModel {

    @ApiModelProperty(value = "总条数")
    private Long total;

    @ApiModelProperty(value = "是否还有评论页")
    private Boolean hasMore;

    @ApiModelProperty(value = "分页信息")
    private List<PostReplyVO> list;
}
