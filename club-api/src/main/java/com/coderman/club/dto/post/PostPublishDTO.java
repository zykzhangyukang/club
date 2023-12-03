package com.coderman.club.dto.post;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ：zhangyukang
 * @date ：2023/12/01 15:47
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostPublishDTO extends BaseModel {


    @ApiModelProperty(value = "帖子所属板块")
    private Long sectionId;

    @ApiModelProperty(value = "帖子标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "提交令牌")
    private String token;
}
