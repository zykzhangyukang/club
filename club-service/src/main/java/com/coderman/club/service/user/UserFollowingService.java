package com.coderman.club.service.user;

import com.coderman.club.model.user.UserFollowingModel;

/**
 * @author zhangyukang
 */
public interface UserFollowingService  {

    /**
     * 根据关注者ID和被关注者ID查询用户关注信息。
     *
     * @param followerId 关注者ID。
     * @param followedId 被关注者ID。
     * @return 包含用户关注信息的对象，如果不存在则返回 null。
     */
    UserFollowingModel selectByUserIdAndFollowed(Long followerId, Long followedId);

    /**
     * 新增用户关注信息。
     *
     * @param record 包含待新增用户关注信息的对象。
     */
    void insertSelective(UserFollowingModel record);

    /**
     * 根据主键更新用户关注信息。
     *
     * @param update 包含更新后用户关注信息的对象。
     */
    void updateByPrimaryKey(UserFollowingModel update);

    /**
     * 根据主键更新用户关注信息中的非空字段。
     *
     * @param update 包含更新后用户关注信息的对象。
     */
    void updateByPrimaryKeySelective(UserFollowingModel update);

    /**
     * 判断是否已关注指定用户。
     *
     * @param userId       当前用户ID。
     * @param targetUserId 目标用户ID。
     * @return 如果已关注返回 true，否则返回 false。
     */
    Boolean isFollowedUser(Long userId, Long targetUserId);

    /**
     * 查询指定用户关注的人数。
     *
     * @param userId 用户ID。
     * @return 用户关注的人数。
     */
    Integer getFollowCountByUserId(Long userId);

}
