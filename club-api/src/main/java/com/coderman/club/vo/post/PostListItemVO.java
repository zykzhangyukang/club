package com.coderman.club.vo.post;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostListItemVO extends BaseModel {

    @ApiModelProperty(value = "主键id")
    private Long postId;

    @ApiModelProperty(value = "发帖用户id")
    private Long userId;

    @ApiModelProperty(value = "帖子所属板块")
    private Long sectionId;

    @ApiModelProperty(value = "帖子标题")
    private String title;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "创建时间")
    private Date createdAt;

    @ApiModelProperty(value = "最后更新时间")
    private Date lastUpdatedAt;
}
