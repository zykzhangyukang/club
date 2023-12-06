package com.coderman.club.timer;

import com.coderman.club.service.serial.SerialNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author coderman
 * @Title: 清除本地缓存定时器
 * @Description: TOD
 * @date 2022/6/2121:38
 */
@Component
public class ClearSerialBufferTimer {

    @Resource
    private SerialNumberService serialNumberService;

    /**
     * 2022-06-21 23:45:00
     * 2022-06-21 23:50:00
     * 2022-06-21 23:55:00
     * 2022-06-22 23:45:00
     * 2022-06-22 23:50:00
     * 2022-06-22 23:55:00
     * 2022-06-23 23:45:00
     * 2022-06-23 23:50:00
     * 2022-06-23 23:55:00
     * 2022-06-24 23:45:00
     */
    @Scheduled(cron = "0 45/5 23 * * ?")
    private void configureTask1() {
       this.serialNumberService.clearSerialBufferMap();
    }


    /**
     * 2022-06-22 00:01:00
     * 2022-06-22 00:02:00
     * 2022-06-22 00:03:00
     * 2022-06-22 00:04:00
     * 2022-06-22 00:05:00
     * 2022-06-22 00:06:00
     * 2022-06-22 00:07:00
     * 2022-06-22 00:08:00
     * 2022-06-22 00:09:00
     * 2022-06-22 00:10:00
     */
    @Scheduled(cron = "0 0-15/5 0 * * ?")
    private void configureTask2() {
        this.serialNumberService.clearSerialBufferMap();
    }

}
