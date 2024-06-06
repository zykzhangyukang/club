package com.coderman.club.service.user.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coderman.club.constant.common.CommonConstant;
import com.coderman.club.constant.common.ResultConstant;
import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.constant.user.CommonConst;
import com.coderman.club.constant.user.UserConstant;
import com.coderman.club.dto.notification.NotifyMsgDTO;
import com.coderman.club.dto.user.UserInfoDTO;
import com.coderman.club.dto.user.UserLoginDTO;
import com.coderman.club.dto.user.UserRegisterDTO;
import com.coderman.club.enums.FileModuleEnum;
import com.coderman.club.enums.NotificationTypeEnum;
import com.coderman.club.enums.SerialTypeEnum;
import com.coderman.club.mapper.user.UserMapper;
import com.coderman.club.model.point.PointAccountModel;
import com.coderman.club.model.user.UserFollowingModel;
import com.coderman.club.model.user.UserInfoModel;
import com.coderman.club.model.user.UserModel;
import com.coderman.club.properties.AuthProperties;
import com.coderman.club.service.notification.NotificationService;
import com.coderman.club.service.point.PointAccountService;
import com.coderman.club.service.post.PostService;
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
import com.google.code.kaptcha.Producer;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author ：zhangyukang
 * @date ：2023/11/20 14:48
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper,UserModel> implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserFollowingService userFollowingService;
    @Resource
    private NotificationService notificationService;
    @Resource
    private PointAccountService pointAccountService;
    @Resource
    private PostService postService;
    @Resource
    private RedisService redisService;
    @Resource
    private RedisLockService redisLockService;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private UserLoginLogService userLoginLogService;
    @Resource
    private AliYunOssUtil aliYunOssUtil;
    @Resource
    private Producer producer;
    @Resource
    private AuthProperties authProperties;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultVO<UserLoginVO> login(UserLoginDTO userLoginDTO) {

        // 获取锁
        final String lockName = RedisKeyConstant.REDIS_LOGIN_LOCK_PREFIX + userLoginDTO.getUsername();
        boolean tryLock = this.redisLockService.tryLock(lockName, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(3));
        if (!tryLock) {
            return ResultUtil.getWarn("登录失败请重试！");
        }

        try {

            // 登录参数校验
            ResultVO<Void> resultVO = this.checkLoginParam(userLoginDTO);
            if (!ResultConstant.RESULT_CODE_200.equals(resultVO.getCode())) {
                return ResultUtil.getWarn(resultVO.getMsg());
            }

            // 验证码验证码校验
            boolean success = this.checkCaptcha(userLoginDTO.getCode(), userLoginDTO.getCaptchaKey(), "login");
            if (!success) {
                return ResultUtil.getWarn("验证码错误！");
            }

            String username = userLoginDTO.getUsername();
            String password = userLoginDTO.getPassword();
            UserModel userModel = this.userMapper.selectByUsername(username);
            if (userModel == null) {

                return ResultUtil.getWarn("用户名或密码错误！");
            }
            if (StringUtils.equals(userModel.getUserStatus(), UserConstant.USER_STATUS_DISABLE)) {
                return ResultUtil.getWarn("用户状态异常，请联系管理员处理！");
            }

            String encryptPwd = MD5Utils.md5HexWithSalt(password, userModel.getSalt());
            if (!StringUtils.equals(encryptPwd, userModel.getPassword())) {

                return ResultUtil.getWarn("用户名或密码错误！");
            }

            // 创建会话
            UserLoginVO userLoginVO = this.createSession(userModel);

            return ResultUtil.getSuccess(UserLoginVO.class, userLoginVO);

        } finally {

            this.redisLockService.unlock(lockName);
        }
    }

    private boolean checkCaptcha(String code, String captchaKey, String captchaType) {
        if (StringUtils.isBlank(captchaKey) || StringUtils.isBlank(code) || StringUtils.isBlank(captchaType)) {
            return false;
        }

        String redisKey;
        if (StringUtils.equals("login", captchaType)) {
            redisKey = RedisKeyConstant.USER_LOGIN_CAPTCHA_PREFIX + captchaKey;
        } else if (StringUtils.equals("register", captchaType)) {
            redisKey = RedisKeyConstant.USER_REGISTER_CAPTCHA_PREFIX + captchaKey;
        } else {
            return false;
        }

        String redisCode = this.redisService.getString(redisKey, RedisDbConstant.REDIS_DB_DEFAULT);
        if (StringUtils.isBlank(redisCode)) {
            return false;
        }
        // 删除验证码
        long del = this.redisService.del(redisKey, RedisDbConstant.REDIS_DB_DEFAULT);
        if (del > 0) {
            log.debug("清空图形验证码. ip:{},captchaType:{},captchaKey:{}", IpUtil.getIp(HttpContextUtil.getHttpServletRequest()), captchaType, captchaKey);
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
        authUserVO.setExpiresIn(authProperties.getTokenExpiration());

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
        String email = userRegisterDTO.getEmail();

        final String lockName = RedisKeyConstant.REDIS_REGISTER_LOCK_PREFIX + userRegisterDTO.getUsername();
        boolean tryLock = this.redisLockService.tryLock(lockName, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(3));
        if (!tryLock) {
            return ResultUtil.getWarn("请勿重复操作！");
        }

        try {

            // 校验注册参数
            ResultVO<Void> resultVO = this.checkRegisterParam(userRegisterDTO);
            if (!ResultConstant.RESULT_CODE_200.equals(resultVO.getCode())) {
                return resultVO;
            }

            // 验证码校验
            boolean success = this.checkCaptcha(userRegisterDTO.getCode(), userRegisterDTO.getCaptchaKey(), "register");
            if (!success) {
                return ResultUtil.getWarn("验证码错误！");
            }

            UserModel userModel = this.userMapper.selectByUsername(username);
            if (null != userModel) {
                return ResultUtil.getWarn("用户名已被注册！");
            }

            Integer emailCount = this.userMapper.selectCount(Wrappers.<UserModel>lambdaQuery().eq(UserModel::getEmail, email));
            if (emailCount > 0) {
                return ResultUtil.getWarn("当前邮箱已被注册！");
            }

            // 初始化用户相关信息
            this.createClubUser(userRegisterDTO);

        } finally {
            this.redisLockService.unlock(lockName);
        }

        return ResultUtil.getSuccess();
    }

    /**
     * 用户初始化接口
     * <p>
     * 1. 创建用户账户记录
     * 2. 创建用户信息记录
     * 3. 创建用户积分账户
     * ....
     *
     * @return
     */
    private UserModel createClubUser(UserRegisterDTO userRegisterDTO) {

        final String salt = RandomStringUtils.randomAlphabetic(32);
        String password = userRegisterDTO.getPassword();
        String username = userRegisterDTO.getUsername();
        String email = userRegisterDTO.getEmail();

        // 用户账号信息
        UserModel registerModel = new UserModel();
        registerModel.setCreateTime(new Date());
        registerModel.setSalt(salt);
        registerModel.setPassword(MD5Utils.md5HexWithSalt(password, salt));
        registerModel.setEmail(email);
        registerModel.setUsername(username);
        registerModel.setNickname(NameUtil.getName());
        registerModel.setUserStatus(UserConstant.USER_STATUS_ENABLE);
        registerModel.setUserCode(SerialNumberUtil.get(SerialTypeEnum.USER_CODE));
        registerModel.setUpdateTime(new Date());
        this.userMapper.insert(registerModel);

        // 用户详情信息
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setUserId(registerModel.getUserId());
        userInfoModel.setUserCode(registerModel.getUserCode());
        userInfoModel.setBio(StringUtils.EMPTY);
        userInfoModel.setLocation(StringUtils.EMPTY);
        userInfoModel.setUserTags(StringUtils.EMPTY);
        userInfoModel.setWebsite(StringUtils.EMPTY);
        userInfoModel.setGender(UserConstant.USER_STATUS_GENDER);
        userInfoModel.setRegisterTime(new Date());
        userInfoModel.setFollowersCount(0L);
        userInfoModel.setFollowingCount(0L);
        userInfoModel.setAvatar(this.generatorAvatar(registerModel));
        this.userInfoService.insertSelective(userInfoModel);

        // 用户积分账户信息
        PointAccountModel pointAccountModel = new PointAccountModel();
        pointAccountModel.setUserId(registerModel.getUserId());
        pointAccountModel.setCreateTime(registerModel.getCreateTime());
        pointAccountModel.setPointsBalance(0);
        pointAccountModel.setUserCode(registerModel.getUserCode());
        this.pointAccountService.insertSelective(pointAccountModel);


        // 用户注册消息欢迎消息
        NotifyMsgDTO notifyMsgDTO = NotifyMsgDTO.builder()
                .senderId(0L)
                .userIdList(Collections.singletonList(registerModel.getUserId()))
                .typeEnum(NotificationTypeEnum.REGISTER_WELCOME)
                .content(String.format(NotificationTypeEnum.REGISTER_WELCOME.getTemplate(), registerModel.getNickname()))
                .build();
        notificationService.send(notifyMsgDTO);

        return registerModel;
    }

    private ResultVO<Void> checkLoginParam(UserLoginDTO userLoginDTO) {
        if (userLoginDTO == null) {
            return ResultUtil.getWarn("参数错误");
        }
        if (StringUtils.isBlank(userLoginDTO.getUsername()) || StringUtils.length(userLoginDTO.getUsername()) > 16) {
            return ResultUtil.getWarn("登录用户名不能为空，且不超过16个字符！");
        }
        if (StringUtils.isBlank(userLoginDTO.getPassword()) || StringUtils.length(userLoginDTO.getPassword()) > 20) {
            return ResultUtil.getWarn("登录密码不能为空，且不超过20个字符！");
        }
        return ResultUtil.getSuccess();
    }

    private ResultVO<Void> checkRegisterParam(UserRegisterDTO userRegisterDTO) {

        if (userRegisterDTO == null) {
            return ResultUtil.getWarn("参数错误");
        }
        if (StringUtils.isBlank(userRegisterDTO.getUsername()) || StringUtils.length(userRegisterDTO.getUsername()) > 16) {
            return ResultUtil.getWarn("注册用户名不能为空，且不超过16个字符！");
        }
        if (StringUtils.isBlank(userRegisterDTO.getPassword()) || StringUtils.length(userRegisterDTO.getPassword()) > 20) {
            return ResultUtil.getWarn("账号登录密码不能为空，且不超过20个字符！");
        }
        if (StringUtils.isBlank(userRegisterDTO.getEmail())) {
            return ResultUtil.getWarn("注册邮箱不能为空！");
        }
        String regex = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        if (!Pattern.matches(regex, userRegisterDTO.getEmail())) {
            return ResultUtil.getWarn("注册邮箱格式不正确！");
        }
        return ResultUtil.getSuccess();
    }

    private String generatorAvatar(UserModel registerModel) {
        String filePath = this.aliYunOssUtil.genFilePath(registerModel.getUserId() + "_avatar.png", FileModuleEnum.USER_MODULE);
        try {

            byte[] bytes = AvatarUtil.create(registerModel.getUserId().hashCode());
            this.aliYunOssUtil.uploadStream(new ByteArrayInputStream(bytes), filePath);
        } catch (Exception e) {
            log.error("生成用户头像失败:{}, 上传失败设置默认头像: {}", e.getMessage(), UserConstant.USER_DEFAULT_AVATAR);
            return UserConstant.USER_DEFAULT_AVATAR;
        }
        return CommonConstant.OSS_DOMAIN + filePath;
    }

    @Override
    public ResultVO<Void> logout(String token) {
        if (!StringUtils.isBlank(token)) {
            AuthUserVO authUserVO = this.redisService.getObject(RedisKeyConstant.USER_ACCESS_TOKEN_PREFIX + token
                    , AuthUserVO.class, RedisDbConstant.REDIS_DB_DEFAULT);
            if (null == authUserVO) {

                return ResultUtil.getSuccess();
            }
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

        if (StringUtils.isBlank(refreshToken)) {
            return ResultUtil.getFail(UserLoginRefreshVO.class, null, "参数错误");
        }

        final String lockName = RedisKeyConstant.REDIS_REFRESH_LOCK_PREFIX + refreshToken;
        boolean tryLock = this.redisLockService.tryLock(lockName, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(3));
        if (!tryLock) {
            return ResultUtil.getWarn("刷新令牌失败请重试！");
        }
        try {

            AuthUserVO oldAuthUserVo = this.redisService.getObject(RedisKeyConstant.USER_REFRESH_TOKEN_PREFIX + refreshToken,
                    AuthUserVO.class, RedisDbConstant.REDIS_DB_DEFAULT);
            if (oldAuthUserVo == null) {
                return ResultUtil.getFail("会话已过期，请重新登录！");
            }

            // 生成新的访问令牌和刷新token
            String newToken = RandomStringUtils.randomAlphabetic(50);
            String newRefreshToken = RandomStringUtils.randomAlphabetic(50);
            UserModel userModel = this.userMapper.selectById(oldAuthUserVo.getUserId());
            if (userModel == null) {

                return ResultUtil.getFail("会话错误，请重新登录！");
            }

            UserLoginRefreshVO refreshVO = new UserLoginRefreshVO();
            String oldToken = oldAuthUserVo.getToken();
            String oldRefreshToken = oldAuthUserVo.getRefreshToken();
            Integer oldExpiresIn = oldAuthUserVo.getExpiresIn();

            // 判断一下之前的token是否还在有效期内 (OR即将到期)
            boolean existsOldToken = this.redisService.exists(RedisKeyConstant.USER_ACCESS_TOKEN_PREFIX + oldAuthUserVo.getToken(), RedisDbConstant.REDIS_DB_DEFAULT);
            if (1 == 1) {

                AuthUserVO authUserVO = this.convertToAuthVO(userModel, newToken, newRefreshToken);

                // 删除原来刷新令牌
                this.redisService.del(RedisKeyConstant.USER_REFRESH_TOKEN_PREFIX + oldRefreshToken, RedisDbConstant.REDIS_DB_DEFAULT);
                // 保存登录令牌 (1天)
                this.redisService.setObject(RedisKeyConstant.USER_ACCESS_TOKEN_PREFIX + newToken, authUserVO, authProperties.getTokenExpiration(), RedisDbConstant.REDIS_DB_DEFAULT);
                // 保存刷新令牌 (7天)
                this.redisService.setObject(RedisKeyConstant.USER_REFRESH_TOKEN_PREFIX + newRefreshToken, authUserVO, authProperties.getRefreshTokenExpiration(), RedisDbConstant.REDIS_DB_DEFAULT);

                refreshVO.setRefreshToken(newRefreshToken);
                refreshVO.setToken(newToken);
                refreshVO.setExpiresIn(authProperties.getTokenExpiration());

            } else {
                refreshVO.setToken(oldToken);
                refreshVO.setRefreshToken(oldRefreshToken);
                refreshVO.setExpiresIn(oldExpiresIn);
            }

            return ResultUtil.getSuccess(UserLoginRefreshVO.class, refreshVO);

        } finally {
            this.redisLockService.unlock(lockName);
        }
    }

    @Override
    public ResultVO<UserLoginVO> getUserInfo() {

        AuthUserVO current = AuthUtil.getCurrent();
        Assert.notNull(current, "用户未登录！");

        Long userId = current.getUserId();
        UserInfoModel userInfoModel = this.userInfoService.selectByUserId(userId);
        UserModel userModel = this.userMapper.selectById(userId);

        Assert.notNull(userModel, "用户不存在！");
        Assert.notNull(userInfoModel, "用户不存在！");

        // 关注的人数
        Integer followCount = this.userFollowingService.getFollowCountByUserId(current.getUserId());
        // 收藏的帖子数
        Integer collectCount = this.postService.getCollectCountByUserId(current.getUserId());

        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setUserId(current.getUserId());
        userLoginVO.setUsername(current.getUsername());
        userLoginVO.setAvatar(userInfoModel.getAvatar());
        userLoginVO.setNickname(userModel.getNickname());
        userLoginVO.setUserCode(userInfoModel.getUserCode());
        userLoginVO.setRefreshToken(current.getRefreshToken());
        userLoginVO.setToken(current.getToken());
        userLoginVO.setExpiresIn(current.getExpiresIn());
        userLoginVO.setFollowCount(followCount);
        userLoginVO.setCollectCount(collectCount);
        return ResultUtil.getSuccess(UserLoginVO.class, userLoginVO);
    }

    @Override
    public ResultVO<Void> updateInfo(UserInfoDTO userInfoDTO) {

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {

            return ResultUtil.getWarn("当前用户未登录！");
        }

        UserInfoModel updateModel = new UserInfoModel();

        updateModel.setUserId(current.getUserId());
        updateModel.setUserTags(StringUtils.join(userInfoDTO.getUserTags(), ","));
        updateModel.setAvatar(userInfoDTO.getAvatar());
        updateModel.setGender(userInfoDTO.getGender());
        updateModel.setWebsite(userInfoDTO.getWebsite());
        updateModel.setBio(userInfoDTO.getBio());
        updateModel.setLocation(userInfoDTO.getLocation());

        // 修改用户账号信息
        this.userMapper.update(null,
                Wrappers.<UserModel>lambdaUpdate()
                        .set(UserModel::getNickname, userInfoDTO.getNickname())
                        .eq(UserModel::getUserId, current.getUserId()));

        // 编辑用户信息
        this.userInfoService.updateUserInfoByUserId(updateModel);

        return ResultUtil.getSuccess();
    }

    @Override
    public ResultVO<String> captcha(String captchaKey, String captchaType) {

        if (StringUtils.isBlank(captchaKey)) {
            return ResultUtil.getWarn("验证码唯一标识不能为空！");
        }

        if (StringUtils.length(captchaKey) > CommonConstant.LENGTH_64) {
            return ResultUtil.getWarn("参数错误！");
        }

        String redisKey;
        if (StringUtils.equals("login", captchaType)) {
            redisKey = RedisKeyConstant.USER_LOGIN_CAPTCHA_PREFIX + captchaKey;
        } else if (StringUtils.equals("register", captchaType)) {
            redisKey = RedisKeyConstant.USER_REGISTER_CAPTCHA_PREFIX + captchaKey;
        } else {
            return ResultUtil.getWarn("参数错误");
        }

        // 验证码生成
        // 1.生成验证码字符
        String text = producer.createText();
        // 2.生成图片
        BufferedImage bi = producer.createImage(text);

        try (FastByteArrayOutputStream fos = new FastByteArrayOutputStream()) {

            ImageIO.write(bi, "jpg", fos);
            // 3.缓存至 redis 中
            // 保存到redis,60秒后过期
            this.redisService.setString(redisKey, text, 60, RedisDbConstant.REDIS_DB_DEFAULT);
            // 4.返回验证码图片的base64编码
            String imgEncode = new String(Base64.getEncoder().encode(fos.toByteArray()));
            fos.flush();
            return ResultUtil.getSuccess(String.class, "data:image/png;base64," + imgEncode);
        } catch (IOException e) {

            return ResultUtil.getWarn("获取验证码失败！");
        }

    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultVO<Void> follow(Long followedId) {

        if (followedId == null) {
            return ResultUtil.getWarn("参数错误！");
        }
        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {
            return ResultUtil.getFail("当前用户未登录！");
        }
        UserModel userModel = this.userMapper.selectById(followedId);
        if (userModel == null) {
            return ResultUtil.getWarn("被关注的人不存在！");
        }
        if (Objects.equals(followedId, current.getUserId())) {
            return ResultUtil.getWarn("不能关注自己！");
        }

        final String lockName = RedisKeyConstant.REDIS_FOLLOW_LOCK_PREFIX + current.getUserId() + ":" + followedId;
        boolean tryLock = this.redisLockService.tryLock(lockName, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(3));
        if (!tryLock) {
            return ResultUtil.getWarn("请勿重复操作！");
        }

        try {

            UserFollowingModel userFollowingModel = this.userFollowingService.selectByUserIdAndFollowed(current.getUserId(), followedId);
            if (userFollowingModel == null) {

                // 第一次关注 - 新增
                UserFollowingModel record = new UserFollowingModel();
                record.setFollowDate(new Date());
                record.setFollowerId(current.getUserId());
                record.setFollowedId(followedId);
                record.setStatus(CommonConst.STATUS_NORMAL);
                this.userFollowingService.insertSelective(record);

                // 关注好友提醒
                NotifyMsgDTO notifyMsgDTO = NotifyMsgDTO.builder()
                        .senderId(current.getUserId())
                        .userIdList(Collections.singletonList(followedId))
                        .typeEnum(NotificationTypeEnum.FOLLOWING_USER)
                        .content(String.format(NotificationTypeEnum.FOLLOWING_USER.getTemplate(), current.getNickname()))
                        .build();
                this.notificationService.send(notifyMsgDTO);

            } else if (StringUtils.equals(userFollowingModel.getStatus(), CommonConst.STATUS_CANCEL)) {

                // 取消关注后再次关注 - 更新
                UserFollowingModel update = new UserFollowingModel();
                update.setFollowId(userFollowingModel.getFollowId());
                update.setStatus(CommonConst.STATUS_NORMAL);
                update.setFollowDate(new Date());
                this.userFollowingService.updateByPrimaryKeySelective(update);

            } else if (StringUtils.equals(userFollowingModel.getStatus(), CommonConst.STATUS_NORMAL)) {

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
        UserModel userModel = this.userMapper.selectById(followedId);
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

            if (StringUtils.equals(userFollowingModel.getStatus(), CommonConst.STATUS_CANCEL)) {
                return ResultUtil.getWarn("已取消关注用户，请勿重复操作！");
            }

            // 取消关注后再次关注 - 更新
            UserFollowingModel update = new UserFollowingModel();
            update.setFollowId(userFollowingModel.getFollowId());
            update.setStatus(CommonConst.STATUS_CANCEL);
            this.userFollowingService.updateByPrimaryKeySelective(update);
        } finally {
            this.redisLockService.unlock(lockName);
        }

        return ResultUtil.getSuccess();
    }

    @Override
    public ResultVO<UserInfoVO> updateInit() {

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {
            return ResultUtil.getWarn("用户未登录！");
        }

        UserInfoModel userInfoModel = this.userInfoService.selectByUserId(current.getUserId());
        if (userInfoModel == null) {
            return ResultUtil.getWarn("用户信息不存在！");
        }

        UserModel userModel = this.userMapper.selectByUsername(current.getUsername());
        if (userModel == null) {
            return ResultUtil.getWarn("用户信息不存在！");
        }

        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUsername(current.getUsername());
        userInfoVO.setNickname(userModel.getNickname());
        userInfoVO.setUserCode(userModel.getUserCode());
        userInfoVO.setUserId(userModel.getUserId());
        userInfoVO.setLocation(userInfoModel.getLocation());
        userInfoVO.setAvatar(userInfoModel.getAvatar());
        userInfoVO.setGender(userInfoModel.getGender());
        userInfoVO.setBio(userInfoModel.getBio());
        userInfoVO.setWebsite(userInfoModel.getWebsite());
        userInfoVO.setUserTags(Arrays.asList(StringUtils.split(userInfoModel.getUserTags(), ",")));
        userInfoVO.setEmail(userModel.getEmail());

        return ResultUtil.getSuccess(UserInfoVO.class, userInfoVO);
    }

    @Override
    public ResultVO<String> uploadAvatar(MultipartFile file) throws IOException {

        AuthUserVO current = AuthUtil.getCurrent();
        if (current == null) {

            return ResultUtil.getWarn("用户未登录！");
        }

        if (file.isEmpty()) {
            return ResultUtil.getWarn("文件不能为空!");
        }
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new IllegalArgumentException("请选择图片文件上传!");
        }

        long fileSizeInBytes = file.getSize();
        long fileSizeInKb = fileSizeInBytes / 1024;
        long fileSizeInMb = fileSizeInKb / 1024;
        if (fileSizeInMb > 1) {
            throw new IllegalArgumentException("文件大小超过限制（最大限制为1MB）");
        }

        String url = this.aliYunOssUtil.genFilePath(file.getOriginalFilename(), FileModuleEnum.USER_MODULE);
        this.aliYunOssUtil.uploadStream(file.getInputStream(), url);

        final String avatarUrl = CommonConstant.OSS_DOMAIN + url;

        // 更新用户头像
        this.userInfoService.updateUserAvatar(current.getUserId(), avatarUrl);

        return ResultUtil.getSuccess(String.class, avatarUrl);
    }

    @Override
    public ResultVO<UserLoginVO> loginByMp(String openId) {

        Assert.notNull(openId, "openId 不能为空！");

        // 判断用户是否存在
        UserModel userModel = this.userMapper.selectOne(Wrappers.<UserModel>lambdaQuery().eq(UserModel::getMpOpenId, openId).last("limit 1"));
        if (userModel == null) {

            String realPwd = RandomStringUtils.randomAlphanumeric(8);
            String randomUserName = RandomStringUtils.randomAlphanumeric(8);

            UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
            userRegisterDTO.setUsername(randomUserName);
            userRegisterDTO.setPassword(realPwd);
            userRegisterDTO.setEmail(randomUserName + "@club.com");
            userRegisterDTO.setMpOpenId(openId);

            // 公众号自动注册逻辑
            userModel = this.createClubUser(userRegisterDTO);

            // 用户注册消息欢迎消息
            NotifyMsgDTO notifyMsgDTO = NotifyMsgDTO.builder()
                    .senderId(0L)
                    .userIdList(Collections.singletonList(userModel.getUserId()))
                    .typeEnum(NotificationTypeEnum.REGISTER_INIT_PWD)
                    .content(String.format(NotificationTypeEnum.REGISTER_INIT_PWD.getTemplate(), userModel.getNickname(), realPwd))
                    .build();
            notificationService.send(notifyMsgDTO);
        }

        UserLoginVO userLoginVO = createSession(userModel);

        return ResultUtil.getSuccess(UserLoginVO.class, userLoginVO);
    }

    @Override
    public List<UserInfoVO> getUserInfoByIdList(List<Long> idList) {

        if(CollectionUtils.isEmpty(idList)){
            return Lists.newArrayList();
        }
        return this.userMapper.getUserInfoByIdList(idList);
    }

    private UserLoginVO createSession(UserModel userModel) {

        String token = RandomStringUtils.randomAlphabetic(50);
        String refreshToken = RandomStringUtils.randomAlphabetic(50);

        Date loginTime = new Date();

        // 会话对象创建
        AuthUserVO authUserVO = this.convertToAuthVO(userModel, token, refreshToken);
        // 保存登录令牌 (1天)
        this.redisService.setObject(RedisKeyConstant.USER_ACCESS_TOKEN_PREFIX + token, authUserVO, authProperties.getTokenExpiration(), RedisDbConstant.REDIS_DB_DEFAULT);
        // 保存刷新令牌 (7天)
        this.redisService.setObject(RedisKeyConstant.USER_REFRESH_TOKEN_PREFIX + refreshToken, authUserVO, authProperties.getRefreshTokenExpiration(), RedisDbConstant.REDIS_DB_DEFAULT);
        // 更新最新登录时间
        this.userInfoService.updateLastLoginTime(authUserVO.getUserId(), loginTime);
        // 保存登录日志
        this.userLoginLogService.insertLoginLog(authUserVO, loginTime);

        UserLoginVO userLoginVO = new UserLoginVO();
        BeanUtils.copyProperties(userModel, userLoginVO);
        userLoginVO.setToken(token);
        userLoginVO.setRefreshToken(refreshToken);
        userLoginVO.setAvatar(authUserVO.getAvatar());
        userLoginVO.setExpiresIn(authProperties.getTokenExpiration());

        // 关注的人数
        Integer followCount = this.userFollowingService.getFollowCountByUserId(userModel.getUserId());
        userLoginVO.setFollowCount(followCount);
        // 收藏的帖子数
        Integer collectCount = this.postService.getCollectCountByUserId(userModel.getUserId());
        userLoginVO.setCollectCount(collectCount);

        return userLoginVO;
    }


}
