package com.coderman.club.service.user.impl;

import com.coderman.club.dao.user.UserInfoDAO;
import com.coderman.club.model.user.UserInfoExample;
import com.coderman.club.model.user.UserInfoModel;
import com.coderman.club.service.common.impl.BaseServiceImpl;
import com.coderman.club.service.user.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author ：zhangyukang
 * @date ：2023/11/22 16:09
 */
@Slf4j
@Service
public class UserInfoServiceImpl extends BaseServiceImpl<UserInfoModel, UserInfoExample> implements UserInfoService {

    @Resource
    private UserInfoDAO userInfoDAO;

    @Override
    public void updateLastLoginTime(Long userId, Date date) {

        if(userId == null || date == null){
            return;
        }
        this.userInfoDAO.updateLastLoginTime(userId, date);
    }

    @Override
    public UserInfoModel selectByUserId(Long userId) {

        if(userId == null){
            return null;
        }
        return this.userInfoDAO.selectByUserId(userId);
    }

    @Override
    public int updateUserInfoByUserId(UserInfoModel userInfoModel) {
        if(userInfoModel == null || userInfoModel.getUserId() == null){
            return 0;
        }
        return this.userInfoDAO.updateUserInfoByUserId(userInfoModel);
    }
}
