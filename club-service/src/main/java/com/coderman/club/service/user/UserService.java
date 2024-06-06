package com.coderman.club.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coderman.club.dto.user.UserInfoDTO;
import com.coderman.club.dto.user.UserLoginDTO;
import com.coderman.club.dto.user.UserRegisterDTO;
import com.coderman.club.model.user.UserModel;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.user.UserInfoVO;
import com.coderman.club.vo.user.UserLoginRefreshVO;
import com.coderman.club.vo.user.UserLoginVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author zhangyukang
 */
public interface UserService extends IService<UserModel> {

    /**
     * 用户登录。
     *
     * @param userLoginDTO 包含用户登录信息的数据传输对象。
     * @return 表示登录操作结果的响应结果对象。
     */
    ResultVO<UserLoginVO> login(UserLoginDTO userLoginDTO);

    /**
     * 用户注册。
     *
     * @param userRegisterDTO 包含用户注册信息的数据传输对象。
     * @return 表示注册操作结果的响应结果对象。
     */
    ResultVO<Void> register(UserRegisterDTO userRegisterDTO);

    /**
     * 退出登录。
     *
     * @param token 用户的登录凭证。
     * @return 表示退出登录操作结果的响应结果对象。
     */
    ResultVO<Void> logout(String token);

    /**
     * 用户刷新登录。
     *
     * @param refreshToken 用户的刷新凭证。
     * @return 表示刷新登录操作结果的响应结果对象。
     */
    ResultVO<UserLoginRefreshVO> refreshToken(String refreshToken);

    /**
     * 获取用户信息。
     *
     * @return 表示获取用户信息操作结果的响应结果对象。
     */
    ResultVO<UserLoginVO> getUserInfo();

    /**
     * 修改用户信息。
     *
     * @param userInfoDTO 包含待修改用户信息的数据传输对象。
     * @return 表示修改用户信息操作结果的响应结果对象。
     */
    ResultVO<Void> updateInfo(UserInfoDTO userInfoDTO);

    /**
     * 获取图形验证码。
     *
     * @param captchaKey  验证码的键。
     * @param captchaType 验证码的类型。
     * @return 表示获取图形验证码操作结果的响应结果对象。
     */
    ResultVO<String> captcha(String captchaKey, String captchaType);

    /**
     * 关注用户。
     *
     * @param userId 被关注用户的ID。
     * @return 表示关注用户操作结果的响应结果对象。
     */
    ResultVO<Void> follow(Long userId);

    /**
     * 取消关注用户。
     *
     * @param userId 被取消关注的用户的ID。
     * @return 表示取消关注用户操作结果的响应结果对象。
     */
    ResultVO<Void> unfollow(Long userId);

    /**
     * 更新用户信息初始化。
     *
     * @return 表示更新用户信息初始化操作结果的响应结果对象。
     */
    ResultVO<UserInfoVO> updateInit();

    /**
     * 上传用户头像。
     *
     * @param file 待上传的用户头像文件。
     * @return 表示上传用户头像操作结果的响应结果对象。
     * @throws IOException 如果上传过程中发生IO错误。
     */
    ResultVO<String> uploadAvatar(MultipartFile file) throws IOException;

    /**
     * 公众号登录。
     *
     * @param openId 公众号用户的唯一标识符。
     * @return 表示公众号登录操作结果的响应结果对象。
     */
    ResultVO<UserLoginVO> loginByMp(String openId);

    /**
     * 根据用户ID列表获取用户信息。
     *
     * @param idList 待获取用户信息的用户ID列表。
     * @return 包含指定用户信息的列表。
     */
    List<UserInfoVO> getUserInfoByIdList(List<Long> idList);

}
