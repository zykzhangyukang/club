package com.coderman.club.service.user.impl;

import com.coderman.club.dao.user.UserFollowingDAO;
import com.coderman.club.model.user.UserFollowingExample;
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
    private UserFollowingDAO userFollowingDAO;

    @Override
    public UserFollowingModel selectByUserIdAndFollowed(Long followerId, Long followedId) {

        if(followerId == null || followedId == null){
            return null;
        }

        UserFollowingExample example = new UserFollowingExample();
        example.createCriteria().andFollowerIdEqualTo(followerId)
                .andFollowedIdEqualTo(followedId);
        List<UserFollowingModel> userFollowingModels = this.userFollowingDAO.selectByExample(example);
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
        this.userFollowingDAO.insertSelective(record);
    }

    @Override
    public void updateByPrimaryKey(UserFollowingModel update) {
        if(update == null){
            return;
        }
        this.userFollowingDAO.updateByPrimaryKey(update);
    }

    @Override
    public void updateByPrimaryKeySelective(UserFollowingModel update) {
        if(update == null){
            return;
        }
        this.userFollowingDAO.updateByPrimaryKeySelective(update);
    }
}
