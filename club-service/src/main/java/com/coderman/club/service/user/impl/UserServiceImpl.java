package com.coderman.club.service.user.impl;

import com.coderman.club.constant.common.CommonConstant;
import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.constant.user.UserConst;
import com.coderman.club.constant.user.UserFollowingConst;
import com.coderman.club.dao.user.UserDAO;
import com.coderman.club.dto.notification.NotifyMsgDTO;
import com.coderman.club.dto.user.UserInfoDTO;
import com.coderman.club.dto.user.UserLoginDTO;
import com.coderman.club.dto.user.UserRegisterDTO;
import com.coderman.club.enums.NotificationTypeEnum;
import com.coderman.club.enums.SerialTypeEnum;
import com.coderman.club.model.user.UserFollowingModel;
import com.coderman.club.model.user.UserInfoModel;
import com.coderman.club.model.user.UserModel;
import com.coderman.club.service.notification.NotificationService;
import com.coderman.club.service.redis.RedisLockService;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.service.user.UserFollowingService;
import com.coderman.club.service.user.UserInfoService;
import com.coderman.club.service.user.UserLoginLogService;
import com.coderman.club.service.user.UserService;
import com.coderman.club.utils.*;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.user.AuthUserVO;
import com.coderman.club.vo.user.UserInfoVO;
import com.coderman.club.vo.user.UserLoginRefreshVO;
import com.coderman.club.vo.user.UserLoginVO;
import com.wf.captcha.SpecCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author ：zhangyukang
 * @date ：2023/11/20 14:48
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserDAO userDAO;

    @Resource
    private UserFollowingService userFollowingService;

    @Resource
    private NotificationService notificationService;

    @Resource
    private RedisService redisService;

    @Resource
    private RedisLockService redisLockService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserLoginLogService userLoginLogService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultVO<UserLoginVO> login(UserLoginDTO userLoginDTO) {

        final String lockName = RedisKeyConstant.REDIS_LOGIN_LOCK_PREFIX + userLoginDTO.getUsername();

        // 获取锁
        boolean tryLock = this.redisLockService.tryLock(lockName, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(3));
        if (!tryLock) {
            return ResultUtil.getWarn("登录失败请重试！");
        }

        try {

            // 验证码验证码校验
            boolean success = this.checkLoginCaptcha(userLoginDTO.getCode(), userLoginDTO.getCaptchaKey());
            if (!success) {
                return ResultUtil.getWarn("验证码错误！");
            }

            String username = userLoginDTO.getUsername();
            String password = userLoginDTO.getPassword();
            UserModel userModel = this.userDAO.selectByUsername(username);
            if (userModel == null) {

                return ResultUtil.getWarn("用户名或密码错误！");
            }
            if (StringUtils.equals(userModel.getUserStatus(), UserConst.USER_STATUS_DISABLE)) {
                return ResultUtil.getWarn("用户状态异常，请联系管理员处理！");
            }

            String encryptPwd = MD5Utils.md5Hex(password.getBytes());
            if (!StringUtils.equals(encryptPwd, userModel.getPassword())) {

                return ResultUtil.getWarn("用户名或密码错误！");
            }

            String token = RandomStringUtils.randomAlphabetic(50);
            String refreshToken = RandomStringUtils.randomAlphabetic(50);

            Date loginTime = new Date();

            // 会话对象创建
            AuthUserVO authUserVO = this.convertToAuthVO(userModel, token, refreshToken);
            // 保存登录令牌 (1天)
            this.redisService.setObject(RedisKeyConstant.USER_ACCESS_TOKEN_PREFIX + token, authUserVO, 60 * 60 * 24, RedisDbConstant.REDIS_DB_DEFAULT);
            // 保存刷新令牌 (7天)
            this.redisService.setObject(RedisKeyConstant.USER_REFRESH_TOKEN_PREFIX + refreshToken, authUserVO, 60 * 60 * 24 * 7, RedisDbConstant.REDIS_DB_DEFAULT);
            // 更新最新登录时间
            this.userInfoService.updateLastLoginTime(authUserVO.getUserId(), loginTime);
            // 保存登录日志
            this.userLoginLogService.insertLoginLog(authUserVO, loginTime);

            UserLoginVO userLoginVO = new UserLoginVO();
            BeanUtils.copyProperties(userModel, userLoginVO);
            userLoginVO.setToken(token);
            userLoginVO.setRefreshToken(refreshToken);
            return ResultUtil.getSuccess(UserLoginVO.class, userLoginVO);

        } finally {

            this.redisLockService.unlock(lockName);
        }
    }

    private boolean checkLoginCaptcha(String code, String captchaKey) {
        if (StringUtils.isBlank(captchaKey) || StringUtils.isBlank(code)) {
            return false;
        }
        String redisCode = this.redisService.getString(RedisKeyConstant.USER_LOGIN_CAPTCHA_PREFIX + captchaKey, RedisDbConstant.REDIS_DB_DEFAULT);
        if (StringUtils.isBlank(redisCode)) {
            return false;
        }
        // 删除验证码
        long del = this.redisService.del(RedisKeyConstant.USER_LOGIN_CAPTCHA_PREFIX + captchaKey, RedisDbConstant.REDIS_DB_DEFAULT);
        if (del > 0) {
            log.warn("校验验证码错误,清空验证码. ip:{},", IpUtil.getIp(HttpContextUtil.getHttpServletRequest()));
        }
        return StringUtils.equalsIgnoreCase(redisCode, code);
    }

    private AuthUserVO convertToAuthVO(UserModel userModel, String token, String refreshToken) {
        if (userModel == null) {
            return null;
        }
        AuthUserVO authUserVO = new AuthUserVO();
        BeanUtils.copyProperties(userModel, authUserVO);
        authUserVO.setToken(token);
        authUserVO.setRefreshToken(refreshToken);

        // 查询用户信息
        UserInfoModel userInfoModel = this.userInfoService.selectByUserId(userModel.getUserId());
        if (userInfoModel != null) {

            authUserVO.setAvatar(userInfoModel.getAvatar());
        }
        return authUserVO;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
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

        // 用户账号信息
        UserModel registerModel = new UserModel();
        registerModel.setCreateTime(new Date());
        registerModel.setPassword(MD5Utils.md5Hex(password.getBytes()));
        registerModel.setEmail(email);
        registerModel.setUsername(username);
        registerModel.setNickname("用户" + RandomStringUtils.randomAlphabetic(5));
        registerModel.setUserStatus(UserConst.USER_STATUS_ENABLE);
        registerModel.setUserCode(SerialNumberUtil.get(SerialTypeEnum.USER_CODE));
        registerModel.setUpdateTime(new Date());
        this.userDAO.insertSelectiveReturnKey(registerModel);

        // 生成头像
        String avatar = generatorAvatar(registerModel);

        // 用户详情信息
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setUserId(registerModel.getUserId());
        userInfoModel.setBio(StringUtils.EMPTY);
        userInfoModel.setLocation(StringUtils.EMPTY);
        userInfoModel.setUserTags(StringUtils.EMPTY);
        userInfoModel.setWebsite(StringUtils.EMPTY);
        userInfoModel.setGender(StringUtils.EMPTY);
        userInfoModel.setRegisterTime(new Date());
        userInfoModel.setFollowersCount(0L);
        userInfoModel.setFollowingCount(0L);
        userInfoModel.setAvatar(AvatarUtil.BASE64_PREFIX + avatar);
        this.userInfoService.insertSelective(userInfoModel);

        // 新用户注册消息欢迎消息 - 延迟1分钟后发送。
        NotifyMsgDTO notifyMsgDTO = NotifyMsgDTO.builder()
                .senderId(0L)
                .userIdList(Collections.singletonList(registerModel.getUserId()))
                .typeEnum(NotificationTypeEnum.REGISTER_WELCOME)
                .content(String.format(NotificationTypeEnum.REGISTER_WELCOME.getTemplate(), registerModel.getNickname()))
                .build();
        DelayQueueUtil.submit(UUID.randomUUID().toString(), notifyMsgDTO, e -> notificationService.saveAndNotify(e), 1000 * 60);

        return ResultUtil.getSuccess();
    }

    private String generatorAvatar(UserModel registerModel) {
        String avatar = StringUtils.EMPTY;
        try {
            avatar = AvatarUtil.createBase64Avatar(registerModel.getUserId().hashCode());
        } catch (Exception e) {
            log.error("生成用户头像失败:{}", e.getMessage(), e);
        }
        return avatar;
    }

    @Override
    public ResultVO<Void> logout(String token) {

        AuthUserVO authUserVO = this.redisService.getObject(RedisKeyConstant.USER_ACCESS_TOKEN_PREFIX + token, AuthUserVO.class, RedisDbConstant.REDIS_DB_DEFAULT);
        if (null != authUserVO) {

            // 删除登录令牌
            this.redisService.del(RedisKeyConstant.USER_ACCESS_TOKEN_PREFIX + token, RedisDbConstant.REDIS_DB_DEFAULT);
            // 删除刷新令牌
            String refreshToken = authUserVO.getRefreshToken();
            this.redisService.del(RedisKeyConstant.USER_REFRESH_TOKEN_PREFIX + refreshToken, RedisDbConstant.REDIS_DB_DEFAULT);
        }
        return ResultUtil.getSuccess();
    }

    @Override
    public ResultVO<UserLoginRefreshVO> refreshToken(String refreshToken) {

        final String lockName = RedisKeyConstant.REDIS_REFRESH_LOCK_PREFIX + refreshToken;
        boolean tryLock = this.redisLockService.tryLock(lockName, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(3));
        if (!tryLock) {

            return ResultUtil.getWarn("刷新令牌失败请重试！");
        }

        try {

            AuthUserVO oldAuthUserVo = this.redisService.getObject(RedisKeyConstant.USER_REFRESH_TOKEN_PREFIX + refreshToken, AuthUserVO.class, RedisDbConstant.REDIS_DB_DEFAULT);
            if (oldAuthUserVo == null) {
                return ResultUtil.getFail("回话已过期，请重新登录！");
            }

            // 生成新的访问令牌和刷新token
            String newToken = RandomStringUtils.randomAlphabetic(50);
            String newRefreshToken = RandomStringUtils.randomAlphabetic(50);
            UserModel userModel = this.userDAO.selectByPrimaryKey(oldAuthUserVo.getUserId());
            if (userModel == null) {

                return ResultUtil.getFail("会话错误，请重新登录！");
            }

            UserLoginRefreshVO refreshVO = new UserLoginRefreshVO();
            String oldToken = oldAuthUserVo.getToken();
            String oldRefreshToken = oldAuthUserVo.getRefreshToken();

            // 判断一下之前的token是否还在有效期内
            boolean existsOldToken = this.redisService.exists(RedisKeyConstant.USER_ACCESS_TOKEN_PREFIX + oldAuthUserVo.getToken(), RedisDbConstant.REDIS_DB_DEFAULT);
            if (!existsOldToken) {

                AuthUserVO authUserVO = this.convertToAuthVO(userModel, newToken, newRefreshToken);

                // 删除原来刷新令牌
                this.redisService.del(RedisKeyConstant.USER_REFRESH_TOKEN_PREFIX + oldRefreshToken, RedisDbConstant.REDIS_DB_DEFAULT);

                // 保存登录令牌 (1天)
                this.redisService.setObject(RedisKeyConstant.USER_ACCESS_TOKEN_PREFIX + newToken, authUserVO, 60 * 60 * 24, RedisDbConstant.REDIS_DB_DEFAULT);
                // 保存刷新令牌 (7天)
                this.redisService.setObject(RedisKeyConstant.USER_REFRESH_TOKEN_PREFIX + newRefreshToken, authUserVO, 60 * 60 * 24 * 7, RedisDbConstant.REDIS_DB_DEFAULT);

                refreshVO.setRefreshToken(newRefreshToken);
                refreshVO.setToken(newToken);

            } else {
                refreshVO.setToken(oldToken);
                refreshVO.setRefreshToken(oldRefreshToken);
            }

            return ResultUtil.getSuccess(UserLoginRefreshVO.class, refreshVO);

        } finally {
            this.redisLockService.unlock(lockName);
        }
    }

    @Override
    public ResultVO<UserInfoVO> getUserInfo(String token) {

        if (StringUtils.isBlank(token)) {

            return ResultUtil.getSuccess(UserInfoVO.class, null);

        } else {

            UserInfoVO userInfoVO = null;
            AuthUserVO authUserVO = this.redisService.getObject(RedisKeyConstant.USER_ACCESS_TOKEN_PREFIX + token, AuthUserVO.class, RedisDbConstant.REDIS_DB_DEFAULT);
            if (authUserVO != null) {
                userInfoVO = new UserInfoVO();
                BeanUtils.copyProperties(authUserVO, userInfoVO);
            }

            return ResultUtil.getSuccess(UserInfoVO.class, userInfoVO);
        }
    }

    @Override
    public ResultVO<Void> updateInfo(UserInfoDTO userInfoDTO) {

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {

            return ResultUtil.getWarn("当前用户未登录！");
        }

        UserInfoModel updateModel = new UserInfoModel();

        updateModel.setUserId(current.getUserId());
        updateModel.setUserTags(userInfoDTO.getUserTags());
        updateModel.setAvatar(userInfoDTO.getAvatar());
        updateModel.setGender(userInfoDTO.getGender());
        updateModel.setWebsite(userInfoDTO.getWebsite());
        updateModel.setBio(userInfoDTO.getBio());
        updateModel.setLocation(userInfoDTO.getLocation());

        // 编辑用户信息
        this.userInfoService.updateUserInfoByUserId(updateModel);

        return ResultUtil.getSuccess();
    }

    @Override
    public ResultVO<String> loginCaptcha(String k) {

        if (StringUtils.isBlank(k)) {
            return ResultUtil.getWarn("验证码唯一标识不能为空！");
        }

        if (StringUtils.length(k) > CommonConstant.LENGTH_64) {
            return ResultUtil.getWarn("参数错误！");
        }

        // 验证码生成
        SpecCaptcha specCaptcha = new SpecCaptcha(100, 32, 5);
        String base64 = specCaptcha.toBase64();
        String code = specCaptcha.text();

        // 保存到redis,30秒后过期
        this.redisService.setString(RedisKeyConstant.USER_LOGIN_CAPTCHA_PREFIX + k, code, 30, RedisDbConstant.REDIS_DB_DEFAULT);
        return ResultUtil.getSuccess(String.class, base64);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultVO<Void> follow(Long followedId) {

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {
            return ResultUtil.getFail("当前用户未登录！");
        }

        if (followedId == null) {
            return ResultUtil.getWarn("参数错误！");
        }
        UserModel userModel = this.userDAO.selectByPrimaryKey(followedId);
        if (userModel == null) {
            return ResultUtil.getWarn("被关注的人不存在！");
        }
        if (Objects.equals(followedId, current.getUserId())) {
            return ResultUtil.getWarn("不能关注自己！");
        }

        final String lockName = RedisKeyConstant.REDIS_FOLLOW_LOCK_PREFIX + current + ":" + followedId;
        boolean tryLock = this.redisLockService.tryLock(lockName, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(3));
        if (!tryLock) {
            return ResultUtil.getWarn("请勿重复操作！");
        }

        // 判断一下当前用户是否关注过该用户
        // 1.  第一次关注，新增关注记录并发送消息通知.
        // 2. 取消关注后再次关注，更新关注状态。
        try {
            UserFollowingModel userFollowingModel = this.userFollowingService.selectByUserIdAndFollowed(current.getUserId(), followedId);
            if (userFollowingModel == null) {

                // 第一次关注 - 新增
                UserFollowingModel record = new UserFollowingModel();
                record.setFollowDate(new Date());
                record.setFollowerId(current.getUserId());
                record.setFollowedId(followedId);
                record.setStatus(UserFollowingConst.FOLLOWING_STATUS_NORMAL);
                this.userFollowingService.insertSelective(record);

            } else if (StringUtils.equals(userFollowingModel.getStatus(), UserFollowingConst.FOLLOWING_STATUS_CANCEL)) {

                // 取消关注后再次关注 - 更新
                UserFollowingModel update = new UserFollowingModel();
                update.setFollowId(userFollowingModel.getFollowId());
                update.setStatus(UserFollowingConst.FOLLOWING_STATUS_NORMAL);
                update.setFollowDate(new Date());
                this.userFollowingService.updateByPrimaryKeySelective(update);

            } else if (StringUtils.equals(userFollowingModel.getStatus(), UserFollowingConst.FOLLOWING_STATUS_NORMAL)) {

                return ResultUtil.getWarn("已关注用户，请勿重复操作！");
            }

        } finally {
            this.redisLockService.unlock(lockName);
        }
        return ResultUtil.getSuccess();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultVO<Void> unfollow(Long followedId) {

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {
            return ResultUtil.getFail("当前用户未登录！");
        }

        if (followedId == null || Objects.equals(followedId, current.getUserId())) {
            return ResultUtil.getWarn("参数错误！");
        }
        UserModel userModel = this.userDAO.selectByPrimaryKey(followedId);
        if (userModel == null) {
            return ResultUtil.getWarn("被关注的人不存在！");
        }

        final String lockName = RedisKeyConstant.REDIS_UNFOLLOW_LOCK_PREFIX + current + ":" + followedId;
        boolean tryLock = this.redisLockService.tryLock(lockName, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(3));
        if (!tryLock) {
            return ResultUtil.getWarn("请勿重复操作！");
        }

        try {

            UserFollowingModel userFollowingModel = this.userFollowingService.selectByUserIdAndFollowed(current.getUserId(), followedId);
            if (userFollowingModel == null) {
                return ResultUtil.getWarn("参数错误！");
            }

            if (StringUtils.equals(userFollowingModel.getStatus(), UserFollowingConst.FOLLOWING_STATUS_CANCEL)) {
                return ResultUtil.getWarn("已取消关注用户，请勿重复操作！");
            }

            // 取消关注后再次关注 - 更新
            UserFollowingModel update = new UserFollowingModel();
            update.setFollowId(userFollowingModel.getFollowId());
            update.setStatus(UserFollowingConst.FOLLOWING_STATUS_CANCEL);
            this.userFollowingService.updateByPrimaryKeySelective(update);
        } finally {
            this.redisLockService.unlock(lockName);
        }

        return ResultUtil.getSuccess();
    }
}
