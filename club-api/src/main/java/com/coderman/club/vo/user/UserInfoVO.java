package com.coderman.club.vo.user;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
}