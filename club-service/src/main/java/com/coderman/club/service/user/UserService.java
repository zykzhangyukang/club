package com.coderman.club.service.user;

import com.coderman.club.dto.user.UserLoginDTO;
import com.coderman.club.dto.user.UserRegisterDTO;
import com.coderman.club.vo.common.ResultVO;

/**
 * @author Administrator
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param userLoginDTO
     * @return
     */
    ResultVO<String> login(UserLoginDTO userLoginDTO);


    /**
     * 用户注册
     *
     * @param userRegisterDTO
     * @return
     */
    ResultVO<Void> register(UserRegisterDTO userRegisterDTO);
}
