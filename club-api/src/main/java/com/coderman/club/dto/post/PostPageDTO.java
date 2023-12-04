package com.coderman.club.dto.post;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostPageDTO extends BaseModel {

    @ApiModelProperty(value = "当前分页")
    private Long currentPage;

    @ApiModelProperty(value = "每页显示条数")
    private Long pageSize;

    @ApiModelProperty(value = "一级分类id")
    private Long firstSectionId;

    @ApiModelProperty(value = "二级分类id")
    private Long secondSectionId;
}
