package com.coderman.club.dto.notification;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NotificationDTO extends BaseModel {

    @ApiModelProperty(value = "当前分页")
    private Long currentPage;

    @ApiModelProperty(value = "每页显示条数")
    private Long pageSize;

    @ApiModelProperty(value = "是否已读")
    private Boolean isRead;

    @ApiModelProperty(value = "类型")
    private String type;
}
