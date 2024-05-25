package com.coderman.club.vo.user;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Administrator
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserLoginVO extends BaseModel {

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "用户编号")
    private String userCode;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "访问令牌")
    private String token;

    @ApiModelProperty(value = "刷新令牌")
    private String refreshToken;

    @ApiModelProperty(value = "关注的人数")
    private Integer followCount;

    @ApiModelProperty(value = "收藏数量")
    private Integer collectCount;
}
