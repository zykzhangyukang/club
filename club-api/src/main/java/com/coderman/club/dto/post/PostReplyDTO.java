package com.coderman.club.dto.post;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostReplyDTO extends BaseModel {

    @ApiModelProperty(value = "offset")
    private Long offsetId;

    @ApiModelProperty(value = "每页显示条数")
    private Long pageSize;

    @ApiModelProperty(value = "评论id")
    private Long commentId;
}
