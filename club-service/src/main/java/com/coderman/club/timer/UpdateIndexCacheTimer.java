package com.coderman.club.timer;

import com.coderman.club.init.IndexCacheInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 首页缓存数据更新
 *
 * @author ：zhangyukang
 * @date ：2023/12/06 12:19
 */
@Component
@Slf4j
public class UpdateIndexCacheTimer {

    @Resource
    private IndexCacheInitializer indexCacheInitializer;

    /**
     * 栏目数据缓存15分钟刷新一次
     */
    @Scheduled(cron = "0 */15 * * * ?")
    public void refreshIndexSectionsCache() {
        this.indexCacheInitializer.initSectionCache();
        log.info("refreshSectionCache#首页栏目缓存数据刷新完成....");
    }

    /**
     * 栏目数据缓存15分钟刷新一次
     */
    @Scheduled(cron = "0 */15 * * * ?")
    public void refreshIndexCarousesCache() {
        this.indexCacheInitializer.initCarouseCache();
        log.info("refreshCarouseCache#首页轮播图数据刷新完成....");
    }


}
