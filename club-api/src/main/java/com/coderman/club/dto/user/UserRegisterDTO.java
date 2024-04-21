package com.coderman.club.dto.user;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ：zhangyukang
 * @date ：2023/11/20 14:49
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserRegisterDTO extends BaseModel {

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "登录密码")
    private String password;

    @ApiModelProperty(value = "邮箱地址")
    private String email;

    @ApiModelProperty(value = "验证码")
    private String code;

    @ApiModelProperty(value = "公众号openId")
    private String mpOpenId;

    @ApiModelProperty(value = "验证码key")
    private String captchaKey;
}
