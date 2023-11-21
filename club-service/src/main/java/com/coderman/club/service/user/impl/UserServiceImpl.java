package com.coderman.club.service.user.impl;

import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisPrefixConstant;
import com.coderman.club.constant.user.UserConstant;
import com.coderman.club.dao.user.UserDAO;
import com.coderman.club.dto.user.UserLoginDTO;
import com.coderman.club.dto.user.UserRegisterDTO;
import com.coderman.club.enums.SerialTypeEnum;
import com.coderman.club.model.user.UserModel;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.service.user.UserService;
import com.coderman.club.utils.MD5Utils;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.utils.SerialNumberUtil;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.user.UserLoginVO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author ：zhangyukang
 * @date ：2023/11/20 14:48
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDAO userDAO;

    @Resource
    private RedisService redisService;

    @Override
    public ResultVO<UserLoginVO> login(UserLoginDTO userLoginDTO) {

        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();
        UserModel userModel = this.userDAO.selectByUsername(username);
        if (userModel == null) {

            return ResultUtil.getWarn("用户名或密码错误！");
        }
        if (StringUtils.equals(userModel.getUserStatus(), UserConstant.USER_STATUS_DISABLE)) {
            return ResultUtil.getWarn("用户状态异常，请联系管理员处理！");
        }

        String encryptPwd = MD5Utils.md5Hex(password.getBytes());
        if (!StringUtils.equals(encryptPwd, userModel.getPassword())) {

            return ResultUtil.getWarn("用户名或密码错误！");
        }

        UserLoginVO userLoginVO = new UserLoginVO();
        BeanUtils.copyProperties(userModel, userLoginVO);

        String token = RandomStringUtils.randomAlphabetic(50);
        String refreshToken = RandomStringUtils.randomAlphabetic(50);
        userLoginVO.setToken(token);
        userLoginVO.setRefreshToken(refreshToken);

        // 保存会话
        this.redisService.setString(RedisPrefixConstant.USER_ACCESS_TOKEN_PREFIX + token, token, 60 * 60, RedisDbConstant.REDIS_DB_DEFAULT);

        return ResultUtil.getSuccess(UserLoginVO.class, userLoginVO);
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
        registerModel.setPassword(MD5Utils.md5Hex(password.getBytes()));
        registerModel.setEmail(email);
        registerModel.setUsername(username);
        registerModel.setNickname("U_" + RandomStringUtils.randomAlphabetic(5));
        registerModel.setUserStatus(UserConstant.USER_STATUS_ENABLE);
        registerModel.setUserCode(SerialNumberUtil.get(SerialTypeEnum.USER_CODE));
        this.userDAO.insertSelective(registerModel);

        return ResultUtil.getSuccess();
    }
}
