package com.coderman.club.service.user.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coderman.club.constant.user.CommonConst;
import com.coderman.club.mapper.user.UserFollowingMapper;
import com.coderman.club.model.user.UserFollowingModel;
import com.coderman.club.service.user.UserFollowingService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ：zhangyukang
 * @date ：2023/11/24 10:04
 */
@Service
public class UserFollowingServiceImpl implements UserFollowingService {

    @Resource
    private UserFollowingMapper userFollowingDAO;

    @Override
    public UserFollowingModel selectByUserIdAndFollowed(Long followerId, Long followedId) {

        if(followerId == null || followedId == null){
            return null;
        }

        List<UserFollowingModel> userFollowingModels = this.userFollowingDAO.selectList(Wrappers.<UserFollowingModel>lambdaQuery()
        .eq(UserFollowingModel::getFollowerId, followerId)
        .eq(UserFollowingModel::getFollowedId, followedId));
        if(CollectionUtils.isNotEmpty(userFollowingModels)){

            return userFollowingModels.get(0);
        }
        return null;
    }

    @Override
    public void insertSelective(UserFollowingModel record) {

        if(record == null){
            return;
        }
        this.userFollowingDAO.insert(record);
    }

    @Override
    public void updateByPrimaryKey(UserFollowingModel update) {
        if(update == null){
            return;
        }
        this.userFollowingDAO.updateById(update);
    }

    @Override
    public void updateByPrimaryKeySelective(UserFollowingModel update) {
        if(update == null){
            return;
        }
        this.userFollowingDAO.updateById(update);
    }

    @Override
    public Boolean isFollowedUser(Long userId, Long targetUserId) {

        if (userId == null || targetUserId == null) {
            return false;
        }


        Integer integer = this.userFollowingDAO.selectCount(Wrappers.<UserFollowingModel>lambdaQuery()
                .eq(UserFollowingModel::getFollowerId, userId)
                .eq(UserFollowingModel::getFollowedId, targetUserId)
                .eq(UserFollowingModel::getStatus, CommonConst.STATUS_NORMAL));

        return integer > 0;
    }

    @Override
    public Integer getFollowCountByUserId(Long userId) {
        if (userId == null) {
            return 0;
        }
        return this.userFollowingDAO.selectCount(Wrappers.<UserFollowingModel>lambdaQuery()
                .eq(UserFollowingModel::getFollowerId, userId)
                .eq(UserFollowingModel::getStatus, CommonConst.STATUS_NORMAL));
    }
}
