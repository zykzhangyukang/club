package com.coderman.club.dto.user;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ：zhangyukang
 * @date ：2023/11/22 18:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInfoDTO extends BaseModel {

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户标签")
    private String userTags;

    @ApiModelProperty(value = "头像地址")
    private String avatar;

    @ApiModelProperty(value = "用户性别")
    private String gender;

    @ApiModelProperty(value = "个人简介")
    private String bio;

    @ApiModelProperty(value = "个人网站")
    private String website;

    @ApiModelProperty(value = "地址位置")
    private String location;


}
