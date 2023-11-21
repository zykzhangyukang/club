package com.coderman.club.dao.user;

import com.coderman.club.dao.common.BaseDAO;
import com.coderman.club.model.user.UserExample;
import com.coderman.club.model.user.UserModel;
import org.apache.ibatis.annotations.Param;

public interface UserDAO extends BaseDAO<UserModel, UserExample> {

    UserModel selectByUsername(@Param(value = "username") String username);

    UserModel selectByEmail(@Param(value = "email") String email);
}