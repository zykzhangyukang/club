package com.coderman.club.controller.notification;

import com.coderman.club.dto.notification.NotificationDTO;
import com.coderman.club.service.notification.NotificationService;
import com.coderman.club.vo.common.PageVO;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.notification.NotificationCountVO;
import com.coderman.club.vo.notification.NotificationVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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
    public ResultVO<NotificationCountVO> getUnReadCount(){

        return this.notificationService.getUnReadCount();
    }

    @ApiOperation(value = "获取消息通知列表")
    @PostMapping(value = "/page")
    public ResultVO<PageVO<List<NotificationVO>>> getPage(@RequestBody NotificationDTO notificationDTO){

        return this.notificationService.getPage(notificationDTO);
    }

    @ApiOperation(value = "已读消息")
    @GetMapping(value = "/read")
    public ResultVO<Void> read(Long notificationId){

        return this.notificationService.read(notificationId);
    }
}
