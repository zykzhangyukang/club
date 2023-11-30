package com.coderman.club.service.user;

import com.coderman.club.model.user.UserInfoModel;
import com.coderman.club.vo.user.UserInfoVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface UserInfoService {


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


    /**
     * 新增
     *
     * @param userInfoModel
     */
    void insertSelective(UserInfoModel userInfoModel);

    /**
     * 获取用户头像
     *
     * @param userIds
     * @return
     */
    Map<Long, UserInfoVO> selectUserInfoVoMap(List<Long> userIds);
}
