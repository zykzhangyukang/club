package com.coderman.club.service.user;

import com.coderman.club.model.user.UserInfoExample;
import com.coderman.club.model.user.UserInfoModel;
import com.coderman.club.service.common.BaseService;

import java.util.Date;

/**
 * @author Administrator
 */
public interface UserInfoService extends BaseService<UserInfoModel, UserInfoExample> {


    /**
     * 更新最新登录时间
     * @param userId
     * @param date
     */
    void updateLastLoginTime(Long userId, Date date);

    /**
     * 获取用户基本信息
     *
     * @param userId
     * @return
     */
    UserInfoModel selectByUserId(Long userId);

    /**
     *
     * 更新用户基本信息
     *
     * @param userInfoModel
     * @return
     */
    int updateUserInfoByUserId(UserInfoModel userInfoModel);
}
