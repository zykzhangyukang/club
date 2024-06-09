package com.coderman.club.service.user.impl;

import com.coderman.club.mapper.user.UserInfoMapper;
import com.coderman.club.model.user.UserInfoModel;
import com.coderman.club.service.user.UserInfoService;
import com.coderman.club.vo.user.UserInfoVO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author ：zhangyukang
 * @date ：2023/11/22 16:09
 */
@Slf4j
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public void updateLastLoginTime(Long userId, Date date) {

        if(userId == null || date == null){
            return;
        }
        this.userInfoMapper.updateLastLoginTime(userId, date);
    }

    @Override
    public UserInfoModel selectByUserId(Long userId) {

        if(userId == null){
            return null;
        }
        return this.userInfoMapper.selectByUserId(userId);
    }

    @Override
    public int updateUserInfoByUserId(UserInfoModel userInfoModel) {
        if(userInfoModel == null || userInfoModel.getUserId() == null){
            return 0;
        }
        return this.userInfoMapper.updateUserInfoByUserId(userInfoModel);
    }

    @Override
    public void insertSelective(UserInfoModel userInfoModel) {
        if(userInfoModel == null){
            return;
        }
        this.userInfoMapper.insert(userInfoModel);
    }

}
