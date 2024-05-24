package com.coderman.club.vo.post;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author ：zhangyukang
 * @date ：2023/12/08 14:32
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostHotVO extends BaseModel {

    @ApiModelProperty(value = "主键id")
    private Long postId;

    @ApiModelProperty(value = "帖子标题")
    private String title;

    @ApiModelProperty(value = "浏览量")
    private Integer viewsCount;

    @ApiModelProperty(value = "点赞量")
    private Integer likesCount;

    @ApiModelProperty(value = "评论量")
    private Integer commentsCount;

    @ApiModelProperty(value = "收藏量")
    private Integer collectsCount;

    @ApiModelProperty(value = "创建时间")
    private Date createdAt;

    @ApiModelProperty(value = "最后更新时间")
    private Date lastUpdatedAt;

    @ApiModelProperty(value = "帖子热度值")
    private BigDecimal hotVal;
}
