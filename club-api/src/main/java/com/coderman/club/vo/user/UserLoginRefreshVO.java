package com.coderman.club.vo.user;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ：zhangyukang
 * @date ：2023/11/22 14:42
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserLoginRefreshVO extends BaseModel {

    @ApiModelProperty(value = "访问令牌")
    private String token;

    @ApiModelProperty(value = "刷新令牌")
    private String refreshToken;

    @ApiModelProperty(value = "Token过期时间(单位s)")
    private Integer expiresIn;
}
