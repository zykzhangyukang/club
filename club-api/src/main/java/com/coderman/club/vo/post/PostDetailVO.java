package com.coderman.club.vo.post;

import com.coderman.club.model.common.BaseModel;
import com.coderman.club.model.section.SectionModel;
import com.coderman.club.vo.section.SectionVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @author ：zhangyukang
 * @date ：2023/12/06 9:38
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostDetailVO extends BaseModel {

    @ApiModelProperty(value = "主键id")
    private Long postId;

    @ApiModelProperty(value = "发帖用户id")
    private Long userId;

    @ApiModelProperty(value = "帖子所属板块")
    private Long sectionId;
    private String sectionName;
    private Long parentSectionId;
    private String parentSectionName;

    @ApiModelProperty(value = "帖子标题")
    private String title;

    @ApiModelProperty(value = "详情")
    private String content;

    @ApiModelProperty(value = "标签")
    private List<String> tags;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "创建时间")
    private Date createdAt;

    @ApiModelProperty(value = "最后更新时间")
    private Date lastUpdatedAt;

    @ApiModelProperty(value = "浏览量")
    private Integer viewsCount;

    @ApiModelProperty(value = "点赞量")
    private Integer likesCount;

    @ApiModelProperty(value = "评论量")
    private Integer commentsCount;

    @ApiModelProperty(value = "收藏量")
    private Integer favoritesCount;

    @ApiModelProperty(value = "是否关注")
    private Boolean isFollowed;
}
