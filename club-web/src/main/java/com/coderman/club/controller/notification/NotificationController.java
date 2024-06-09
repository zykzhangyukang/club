package com.coderman.club.controller.notification;

import com.coderman.club.annotation.RateLimit;
import com.coderman.club.dto.notification.NotificationDTO;
import com.coderman.club.limiter.LimiterStrategy;
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
 * @author zhangyukang
 */
@Api(value = "社区通知模块", tags = {"社区通知模块"})
@RestController
@RequestMapping(value = "/api/notification")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @ApiOperation(value = "获取未读消息数")
    @GetMapping(value = "/count")
    public ResultVO<NotificationCountVO> getUnReadCount() {

        return this.notificationService.getUnReadCount();
    }

    @ApiOperation(value = "获取消息通知列表")
    @PostMapping(value = "/page")
    @RateLimit(strategy = LimiterStrategy.SLIDING_WINDOW, windowRequests = 10)
    public ResultVO<PageVO<List<NotificationVO>>> getPage(@RequestBody NotificationDTO notificationDTO) {
        return this.notificationService.getPage(notificationDTO);
    }

    @ApiOperation(value = "已读消息")
    @GetMapping(value = "/read")
    public ResultVO<Void> read(Long notificationId) {

        return this.notificationService.read(notificationId);
    }


    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/delete/{notificationId}")
    public ResultVO<Void> delete(@PathVariable(value = "notificationId") Long notificationId) {
        return this.notificationService.delete(notificationId);
    }
}
