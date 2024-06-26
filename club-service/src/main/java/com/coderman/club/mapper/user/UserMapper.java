package com.coderman.club.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coderman.club.model.user.UserModel;
import com.coderman.club.vo.user.UserInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * This is the base Mapper class for table: club_user
 * Generated by MyBatis Generator.
 *
 * @author coderman - MyBatis Generator
 */
public interface UserMapper extends BaseMapper<UserModel> {

    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @return
     */
    UserModel selectByUsername(@Param(value = "username") String username);

    /**
     * 获取用户信息
     *
     * @param idList
     * @return
     */
    List<UserInfoVO> getUserInfoByIdList(@Param(value = "idList") List<Long> idList);

    /**
     * 更新用户头像
     * @param userId
     * @param avatarUrl
     */
    void updateUserAvatar(@Param(value = "userId") Long userId,@Param(value = "avatarUrl") String avatarUrl);
}