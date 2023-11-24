package com.coderman.club.service.user;

import com.coderman.club.vo.user.AuthUserVO;

import java.util.Date;

/**
 * @author Administrator
 */
public interface UserLoginLogService {


    void insertLoginLog(AuthUserVO authUserVO, Date loginTime);
}
