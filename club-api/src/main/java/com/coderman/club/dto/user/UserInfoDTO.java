package com.coderman.club.dto.user;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

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
    @Length(max = 512,message = "用户标签不超过512个字符")
    private String userTags;

    @ApiModelProperty(value = "头像地址")
    @Length(max = 512,message = "头像地址不超过512个字符")
    private String avatar;

    @ApiModelProperty(value = "用户性别")
    private String gender;

    @ApiModelProperty(value = "个人简介")
    @Length(max = 128,message = "个人简介不超过128个字符")
    private String bio;

    @ApiModelProperty(value = "个人网站")
    @Length(max = 128,message = "个人网站不超过128个字符")
    private String website;

    @ApiModelProperty(value = "地址位置")
    @Length(max = 16,message = "地址位置不超过16个字符")
    private String location;
}
