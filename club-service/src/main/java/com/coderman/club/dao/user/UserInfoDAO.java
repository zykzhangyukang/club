package com.coderman.club.dao.user;

import com.coderman.club.dao.common.BaseDAO;
import com.coderman.club.model.user.UserInfoExample;
import com.coderman.club.model.user.UserInfoModel;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @author Administrator
 */
public interface UserInfoDAO extends BaseDAO<UserInfoModel, UserInfoExample> {

    int updateLastLoginTime(@Param(value = "userId") Long userId,@Param(value = "loginTime") Date date);

    UserInfoModel selectByUserId(@Param(value = "userId") Long userId);

}