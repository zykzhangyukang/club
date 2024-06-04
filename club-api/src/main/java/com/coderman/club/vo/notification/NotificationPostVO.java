package com.coderman.club.vo.notification;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ：zhangyukang
 * @date ：2024/06/04 11:50
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NotificationPostVO extends BaseModel {

    @ApiModelProperty(value = "帖子标题")
    private Long postId;

    @ApiModelProperty(value = "帖子标题")
    private String postTitle;
}
