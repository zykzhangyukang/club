package com.coderman.club.service.user.impl;

import com.coderman.club.dto.user.UserLoginDTO;
import com.coderman.club.dto.user.UserRegisterDTO;
import com.coderman.club.service.user.UserService;
import com.coderman.club.vo.common.ResultVO;
import org.springframework.stereotype.Service;

/**
 * @author ：zhangyukang
 * @date ：2023/11/20 14:48
 */
@Service
public class UserServiceImpl implements UserService {

    @Override
    public ResultVO<String> login(UserLoginDTO userLoginDTO) {

        return null;
    }

    @Override
    public ResultVO<String> register(UserRegisterDTO userRegisterDTO) {
        return null;
    }
}
