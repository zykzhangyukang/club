package com.coderman.club.dao.user;

import com.coderman.club.dao.common.BaseDAO;
import com.coderman.club.model.user.UserInfoExample;
import com.coderman.club.model.user.UserInfoModel;
import com.coderman.club.vo.user.UserInfoVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface UserInfoDAO extends BaseDAO<UserInfoModel, UserInfoExample> {

    int updateLastLoginTime(@Param(value = "userId") Long userId,@Param(value = "loginTime") Date date);

    UserInfoModel selectByUserId(@Param(value = "userId") Long userId);

    int updateUserInfoByUserId(UserInfoModel userInfoModel);

    @MapKey(value = "userId")
    Map<Long, UserInfoVO> selectUserInfoVoMap(@Param(value = "userIds") List<Long> userIds);
}