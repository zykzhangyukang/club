package com.coderman.club.dto.user;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

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
    @NotBlank(message = "用户头像不能不能为空")
    private String avatar;

    @ApiModelProperty(value = "用户性别")
    @Pattern(regexp = "^(male|female|other)$", message = "用户性别参数错误  male|female|other")
    private String gender;

    @ApiModelProperty(value = "个人简介")
    private String bio;

    @ApiModelProperty(value = "个人网站")
    private String website;

    @ApiModelProperty(value = "地址位置")
    private String location;
}
