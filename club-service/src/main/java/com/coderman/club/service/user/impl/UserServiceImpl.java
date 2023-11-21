package com.coderman.club.service.user.impl;

import com.coderman.club.constant.user.UserConstant;
import com.coderman.club.dao.user.UserDAO;
import com.coderman.club.dto.user.UserLoginDTO;
import com.coderman.club.dto.user.UserRegisterDTO;
import com.coderman.club.model.user.UserModel;
import com.coderman.club.service.user.UserService;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.vo.common.ResultVO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author ：zhangyukang
 * @date ：2023/11/20 14:48
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDAO userDAO;

    @Override
    public ResultVO<String> login(UserLoginDTO userLoginDTO) {

        return null;
    }

    @Override
    public ResultVO<Void> register(UserRegisterDTO userRegisterDTO) {

        String username = userRegisterDTO.getUsername();
        String password = userRegisterDTO.getPassword();
        String email = userRegisterDTO.getEmail();

        UserModel userModel = this.userDAO.selectByUsername(username);
        if (null != userModel) {

            return ResultUtil.getWarn("当前用户名已被注册！");
        }

        UserModel emailModel = this.userDAO.selectByEmail(email);
        if (null != emailModel) {

            return ResultUtil.getWarn("当前邮箱已被注册！");
        }

        UserModel registerModel = new UserModel();
        registerModel.setCreateTime(new Date());
        registerModel.setEmail(email);
        registerModel.setUsername(username);
        registerModel.setNickname(StringUtils.EMPTY);
        registerModel.setPassword(password);
        registerModel.setNickname(RandomStringUtils.randomAlphabetic(5));
        registerModel.setUserStatus(UserConstant.USER_STATUS_ENABLE);
        this.userDAO.insertSelective(registerModel);

        return ResultUtil.getSuccess();
    }
}
