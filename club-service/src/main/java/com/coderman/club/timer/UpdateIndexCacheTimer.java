package com.coderman.club.timer;

import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.service.carouse.CarouseService;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.service.section.SectionService;
import com.coderman.club.vo.carouse.CarouseVO;
import com.coderman.club.vo.section.SectionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 首页缓存数据更新
 *
 * @author ：zhangyukang
 * @date ：2023/12/06 12:19
 */
@Component
@Slf4j
public class UpdateIndexCacheTimer implements CommandLineRunner {

    @Resource
    private SectionService sectionService;

    @Resource
    private RedisService redisService;

    @Resource
    private CarouseService carouseService;

    @Override
    public void run(String... args) {

        this.refreshIndexSectionsCache();

        this.refreshIndexCarousesCache();
    }

    /**
     * 栏目数据缓存15分钟刷新一次
     */
    @Scheduled(cron = "0 */15 * * * ?")
    public void refreshIndexSectionsCache() {
        this.initSectionCache();
        log.info("refreshSectionCache#首页栏目缓存数据刷新完成....");
    }

    /**
     * 栏目数据缓存15分钟刷新一次
     */
    @Scheduled(cron = "0 */15 * * * ?")
    public void refreshIndexCarousesCache() {
        this.initCarouseCache();
        log.info("refreshCarouseCache#首页轮播图数据刷新完成....");
    }

    /**
     * 首页轮播图数据加载
     */
    private void initCarouseCache() {

        final String tempKey = RedisKeyConstant.REDIS_CAROUSE_CACHE + "_" + System.currentTimeMillis();
        List<CarouseVO> carouselVoList = this.carouseService. getCarouselVoList();
        this.redisService.setListData(tempKey, carouselVoList, RedisDbConstant.REDIS_BIZ_CACHE);
        // 重命名key
        this.redisService.rename(tempKey, RedisKeyConstant.REDIS_CAROUSE_CACHE, RedisDbConstant.REDIS_BIZ_CACHE);
    }

    /**
     * 首页栏目缓存数据加载
     */
    private void initSectionCache() {

        final String tempKey = RedisKeyConstant.REDIS_SECTION_CACHE + "_" + System.currentTimeMillis();
        List<SectionVO> sectionVOList = this.sectionService.getSectionVoList();
        this.redisService.setListData(tempKey, sectionVOList, RedisDbConstant.REDIS_BIZ_CACHE);
        // 重命名key
        this.redisService.rename(tempKey, RedisKeyConstant.REDIS_SECTION_CACHE, RedisDbConstant.REDIS_BIZ_CACHE);
    }
}
