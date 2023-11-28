package com.coderman.club.controller.user;

import com.coderman.club.annotation.RateLimit;
import com.coderman.club.dto.user.UserInfoDTO;
import com.coderman.club.dto.user.UserLoginDTO;
import com.coderman.club.dto.user.UserRegisterDTO;
import com.coderman.club.service.user.UserService;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.user.UserInfoVO;
import com.coderman.club.vo.user.UserLoginRefreshVO;
import com.coderman.club.vo.user.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author ：zhangyukang
 * @date ：2023/11/20 14:16
 */
@Api(value = "社区用户模块", tags = {"社区用户模块"})
@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation(value = "用户登录")
    @PostMapping(value = "/login")
    @RateLimit
    public ResultVO<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {

        return this.userService.login(userLoginDTO);
    }

    @ApiOperation(value = "关注用户")
    @PostMapping(value = "/follow/{userId}")
    public ResultVO<Void> follow(@PathVariable(value = "userId")  Long userId){

        return this.userService.follow(userId);
    }

    @ApiOperation(value = "取消关注用户")
    @PostMapping(value = "/unfollow/{userId}")
    public ResultVO<Void> unfollow(@PathVariable(value = "userId")  Long userId){

        return this.userService.unfollow(userId);
    }

    @ApiOperation(value = "获取验证码")
    @GetMapping(value = "/captcha")
    @RateLimit(replenishRate = 2, burstCapacity = 16)
    public ResultVO<String> captcha(String k,String t) {

        return this.userService.captcha(k, t);
    }


    @ApiOperation(value = "修改用户信息")
    @PutMapping(value = "/update/info")
    public ResultVO<Void> updateInfo(@RequestBody UserInfoDTO userInfoDTO) {

        return this.userService.updateInfo(userInfoDTO);
    }

    @ApiOperation(value = "修改用户信息")
    @GetMapping(value = "/update/init")
    public ResultVO<UserInfoVO> updateInit() {

        return this.userService.updateInit();
    }

    @ApiOperation(value = "获取用户信息")
    @GetMapping(value = "/info")
    public ResultVO<UserInfoVO> getUserInfo(String token) {

        return this.userService.getUserInfo(token);
    }

    @ApiOperation(value = "刷新令牌")
    @GetMapping(value = "/refresh/token")
    @RateLimit(replenishRate = 2, burstCapacity = 8)
    public ResultVO<UserLoginRefreshVO> refreshToken(String refreshToken) {
        return this.userService.refreshToken(refreshToken);
    }

    @ApiOperation(value = "注销登录")
    @PostMapping(value = "/logout")
    public ResultVO<Void> logout(String token) {

        return this.userService.logout(token);
    }

    @ApiOperation(value = "用户注册")
    @PostMapping(value = "/register")
    @RateLimit
    public ResultVO<Void> register(@RequestBody UserRegisterDTO userRegisterDTO) {

        return this.userService.register(userRegisterDTO);
    }

}
