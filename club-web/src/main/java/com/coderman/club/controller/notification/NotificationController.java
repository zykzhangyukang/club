package com.coderman.club.controller.notification;

import com.coderman.club.service.notification.NotificationService;
import com.coderman.club.vo.common.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@Api(value = "社区通知模块", tags = {"社区通知模块"})
@RestController
@RequestMapping(value = "/api/notification")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @ApiOperation(value = "获取未读消息数")
    @GetMapping(value = "/getUnReadCount")
    public ResultVO<Map<String,Object>> getUnReadCount(){

        return this.notificationService.getUnReadCount();
    }
}
