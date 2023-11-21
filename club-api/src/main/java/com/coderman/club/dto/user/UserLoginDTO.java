package com.coderman.club.dto.user;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author ：zhangyukang
 * @date ：2023/11/20 14:45
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserLoginDTO extends BaseModel {

    @ApiModelProperty(value = "用户名")
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "[A-Za-z0-9_]+", message = "用户名格式有误 (大小写字母或数字)")
    @Size(max = 16, message = "用户名不能大于16位")
    private String username;

    @ApiModelProperty(value = "登录密码")
    @NotBlank(message = "登录密码不能为空！")
    @Size(max = 20, message = "登录密码不能大于20位")
    private String password;
}
