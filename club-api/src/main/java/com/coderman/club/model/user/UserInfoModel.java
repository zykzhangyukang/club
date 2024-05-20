package com.coderman.club.model.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * This is the base record class for table: club_user_info
 * Generated by MyBatis Generator.
 * @author coderman - MyBatis Generator
 */
@Data
@ApiModel(value="UserInfoModel", description = "club_user_info 实体类")
@TableName(value = "club_user_info")
public class UserInfoModel implements Serializable {

    @TableId(value = "user_info_id",type = IdType.AUTO)
    @ApiModelProperty(value = "用户信息id")
    private Long userInfoId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户编号")
    private String userCode;

    @ApiModelProperty(value = "用户标签")
    private String userTags;

    @ApiModelProperty(value = "头像地址")
    private String avatar;

    @ApiModelProperty(value = "用户性别")
    private String gender;

    @ApiModelProperty(value = "个人简介")
    private String bio;

    @ApiModelProperty(value = "个人网站")
    private String website;

    @ApiModelProperty(value = "地址位置")
    private String location;

    @ApiModelProperty(value = "关注者数量")
    private Long followersCount;

    @ApiModelProperty(value = "关注的人数量")
    private Long followingCount;

    @ApiModelProperty(value = "注册时间")
    private Date registerTime;

    @ApiModelProperty(value = "最新登录时间")
    private Date lastLoginTime;
}