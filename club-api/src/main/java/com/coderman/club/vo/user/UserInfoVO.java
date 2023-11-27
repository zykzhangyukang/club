package com.coderman.club.vo.user;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author ：zhangyukang
 * @date ：2023/11/22 15:20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInfoVO extends BaseModel {

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "用户编号")
    private String userCode;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "地理位置")
    private String location;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "个人简介")
    private String bio;

    @ApiModelProperty(value = "个人网站")
    private String website;

    @ApiModelProperty(value = "用户标签")
    private List<String> userTags;

    @ApiModelProperty(value = "邮箱")
    private String email;

}
