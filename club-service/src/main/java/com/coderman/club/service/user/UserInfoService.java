package com.coderman.club.service.user;

import com.coderman.club.model.user.UserInfoModel;

import java.util.Date;

/**
 * @author Administrator
 */
public interface UserInfoService {

    /**
     * 新增用户信息
     *
     * @param userInfoModel
     */
    void insertSelective(UserInfoModel userInfoModel);

    /**
     * 更新最新登录时间
     * @param userId
     * @param date
     */
    void updateLastLoginTime(Long userId, Date date);
}
