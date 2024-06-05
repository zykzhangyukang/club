package com.coderman.club.service.user;

import com.coderman.club.vo.user.AuthUserVO;

import java.util.Date;

/**
 * @author Administrator
 */
public interface UserLoginLogService {


    /**
     * 插入登录日志。
     *
     * @param authUserVO 用户认证信息。
     * @param loginTime  登录时间。
     */
    void insertLoginLog(AuthUserVO authUserVO, Date loginTime);

}
