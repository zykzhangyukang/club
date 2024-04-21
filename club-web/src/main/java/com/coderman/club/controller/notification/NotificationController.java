package com.coderman.club.controller.notification;

import com.coderman.club.dto.notification.NotificationDTO;
import com.coderman.club.service.notification.NotificationService;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.notification.NotificationVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@Api(value = "社区通知模块", tags = {"社区通知模块"})
@RestController
@RequestMapping(value = "/api/notification")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @ApiOperation(value = "获取未读消息数")
    @GetMapping(value = "/count")
    public ResultVO<Map<String,Object>> getUnReadCount(){

        return this.notificationService.getUnReadCount();
    }

    @ApiOperation(value = "获取消息通知列表")
    @PostMapping(value = "/page")
    public ResultVO<List<NotificationVO>> getPage(@RequestBody NotificationDTO notificationDTO){

        return this.notificationService.getPage(notificationDTO);
    }

    @ApiOperation(value = "已读消息")
    @GetMapping(value = "/read")
    public ResultVO<Void> read(Long notificationId){

        return this.notificationService.read(notificationId);
    }
}
