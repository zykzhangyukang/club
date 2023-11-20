package com.coderman.club.controller;

import com.coderman.club.dto.user.UserLoginDTO;
import com.coderman.club.dto.user.UserRegisterDTO;
import com.coderman.club.service.user.UserService;
import com.coderman.club.vo.common.ResultVO;
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
    public ResultVO<String> login(@RequestBody @Validated UserLoginDTO userLoginDTO){

        return this.userService.login(userLoginDTO);
    }

    @ApiOperation(value = "用户注册")
    @PostMapping(value = "/register")
    public ResultVO<String> register(@RequestBody UserRegisterDTO userRegisterDTO){

        return this.userService.register(userRegisterDTO);
    }

}
