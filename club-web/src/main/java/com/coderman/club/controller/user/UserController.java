package com.coderman.club.controller.user;

import com.coderman.club.dto.user.UserLoginDTO;
import com.coderman.club.dto.user.UserRegisterDTO;
import com.coderman.club.service.user.UserService;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.user.UserInfoVO;
import com.coderman.club.vo.user.UserLoginRefreshVO;
import com.coderman.club.vo.user.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
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
    public ResultVO<UserLoginVO> login(@RequestBody @Validated UserLoginDTO userLoginDTO) {

        return this.userService.login(userLoginDTO);
    }

    @ApiOperation(value = "获取用户信息")
    @GetMapping(value = "/info")
    public ResultVO<UserInfoVO> getUserInfo(String token){

        return this.userService.getUserInfo(token);
    }

    @ApiOperation(value = "刷新令牌")
    @GetMapping(value = "/refresh/token")
    public ResponseEntity<ResultVO<UserLoginRefreshVO>> refreshToken(String refreshToken) {
        return this.userService.refreshToken(refreshToken);
    }

    @ApiOperation(value = "注销登录")
    @GetMapping(value = "/logout")
    public ResultVO<Void> logout(String token) {

        return this.userService.logout(token);
    }

    @ApiOperation(value = "用户注册")
    @PostMapping(value = "/register")
    public ResultVO<Void> register(@RequestBody @Validated UserRegisterDTO userRegisterDTO) {

        return this.userService.register(userRegisterDTO);
    }

}
