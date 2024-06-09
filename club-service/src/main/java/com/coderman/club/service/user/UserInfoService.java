package com.coderman.club.service.user;

import com.coderman.club.model.user.UserInfoModel;
import com.coderman.club.vo.user.UserInfoVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhangyukang
 */
public interface UserInfoService {


    /**
     * 更新指定用户的最新登录时间。
     *
     * @param userId 用户ID。
     * @param date   最新登录时间。
     */
    void updateLastLoginTime(Long userId, Date date);

    /**
     * 根据用户ID获取用户的基本信息。
     *
     * @param userId 用户ID。
     * @return 包含用户基本信息的对象。
     */
    UserInfoModel selectByUserId(Long userId);

    /**
     * 更新指定用户的基本信息。
     *
     * @param userInfoModel 包含更新后用户基本信息的对象。
     * @return 受影响的行数。
     */
    int updateUserInfoByUserId(UserInfoModel userInfoModel);

    /**
     * 新增用户基本信息。
     *
     * @param userInfoModel 包含待新增用户基本信息的对象。
     */
    void insertSelective(UserInfoModel userInfoModel);
}
