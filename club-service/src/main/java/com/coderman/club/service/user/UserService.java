package com.coderman.club.service.user;

import com.coderman.club.dto.user.UserInfoDTO;
import com.coderman.club.dto.user.UserLoginDTO;
import com.coderman.club.dto.user.UserRegisterDTO;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.user.UserInfoVO;
import com.coderman.club.vo.user.UserLoginRefreshVO;
import com.coderman.club.vo.user.UserLoginVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    ResultVO<UserLoginVO> login(UserLoginDTO userLoginDTO);


    /**
     * 用户注册
     *
     * @param userRegisterDTO
     * @return
     */
    ResultVO<Void> register(UserRegisterDTO userRegisterDTO);

    /**
     * 退出登录
     *
     * @param token
     * @return
     */
    ResultVO<Void> logout(String token);


    /**
     * 用户刷新登录
     *
     * @param refreshToken
     * @return
     */
    ResultVO<UserLoginRefreshVO> refreshToken(String refreshToken);


    /**
     * 获取用户信息
     *
     * @param token
     * @return
     */
    ResultVO<UserLoginVO> getUserLoginInfo(String token);

    /**
     * 修改用户信息
     *
     * @param userInfoDTO
     * @return
     */
    ResultVO<Void> updateInfo(UserInfoDTO userInfoDTO);


    /**
     * 获取图形验证码
     *
     * @param captchaKey
     * @param captchaType
     * @return
     */
    ResultVO<String> captcha(String captchaKey,String captchaType);


    /**
     * 关注用户
     *
     * @param userId
     * @return
     */
    ResultVO<Void> follow(Long userId);

    /**
     * 取消关注用户
     *
     * @param userId
     * @return
     */
    ResultVO<Void> unfollow(Long userId);


    /**
     * 更新用户信息初始化
     *
     * @return
     */
    ResultVO<UserInfoVO> updateInit();

    /**
     * 上传用户头像
     * @param file
     * @return
     */
    ResultVO<String> uploadAvatar(MultipartFile file) throws IOException;
}
