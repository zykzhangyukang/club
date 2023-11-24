package com.coderman.club.service.user;

import com.coderman.club.model.user.UserFollowingModel;

/**
 * @author Administrator
 */
public interface UserFollowingService  {

    /**
     * 查询用户关注信息
     *
     * @param followerId
     * @param followedId
     * @return
     */
    UserFollowingModel selectByUserIdAndFollowed(Long followerId, Long followedId);

    /**
     * 新增
     *
     * @param record
     */
    void insertSelective(UserFollowingModel record);

    /**
     * 更新
     *
     * @param update
     */
    void updateByPrimaryKey(UserFollowingModel update);

    /**
     * 更新
     *
     * @param update
     */
    void updateByPrimaryKeySelective(UserFollowingModel update);
}
