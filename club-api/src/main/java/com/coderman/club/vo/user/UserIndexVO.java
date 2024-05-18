package com.coderman.club.vo.user;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserIndexVO extends BaseModel {

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "用户编号")
    private String userCode;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "头像")
    private String avatar;

}
