package com.coderman.club.dto.user;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * @author ：zhangyukang
 * @date ：2023/11/20 14:45
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserLoginDTO extends BaseModel {

    @ApiModelProperty(value = "用户名")
    @NotBlank(message = "用户名不能为空！")
    private String username;

    @ApiModelProperty(value = "密码")
    @NotBlank(message = "登录密码不能为空！")
    private String password;
}
