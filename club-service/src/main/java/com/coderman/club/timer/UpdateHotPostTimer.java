package com.coderman.club.timer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 计算帖子热度
 *
 * @author ：zhangyukang
 * @date ：2023/12/06 12:17
 */
@Component
@Slf4j
public class UpdateHotPostTimer {



    @Scheduled(cron = "0/10 * * * * ?")
    public void refreshHotPosts(){

    }
}
