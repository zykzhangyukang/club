package com.coderman.club.init;

import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.service.carouse.CarouseService;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.service.section.SectionService;
import com.coderman.club.vo.carouse.CarouseVO;
import com.coderman.club.vo.section.SectionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 首页缓存初始化
 *
 * @author Administrator
 */
@Lazy(value = false)
@Component
@Slf4j
public class IndexCacheInitializer {

    @Resource
    private SectionService sectionService;

    @Resource
    private RedisService redisService;

    @Resource
    private CarouseService carouseService;

    public void init() {

        try {

            this.initSectionCache();
            log.info("首页栏目缓存数据加载完成。。。");

            this.initCarouseCache();
            log.info("首页轮播图缓存加载完成。。。");

            this.initHotPostCache();
            log.info("首页热门帖子标签加载完成。。。");


        } catch (Exception e) {
            log.error("缓存数据加载失败：{}", e.getMessage(), e);
        }
    }

    private void initHotPostCache() {



    }

    /**
     * 首页轮播图数据加载
     */
    public void initCarouseCache() {

        final String tempKey = RedisKeyConstant.REDIS_CAROUSE_CACHE + "_" + System.currentTimeMillis();
        List<CarouseVO> carouselVoList = this.carouseService.getCarouselVoList();
        this.redisService.setListData(tempKey, carouselVoList, RedisDbConstant.REDIS_BIZ_CACHE);
        // 重命名key
        this.redisService.rename(tempKey,RedisKeyConstant.REDIS_CAROUSE_CACHE, RedisDbConstant.REDIS_BIZ_CACHE);
    }

    /**
     * 首页栏目缓存数据加载
     */
    public void initSectionCache() {

        final String tempKey = RedisKeyConstant.REDIS_SECTION_CACHE + "_" + System.currentTimeMillis();
        List<SectionVO> sectionVOList = this.sectionService.getSectionVoList();
        this.redisService.setListData(tempKey, sectionVOList, RedisDbConstant.REDIS_BIZ_CACHE);
        // 重命名key
        this.redisService.rename(tempKey,RedisKeyConstant.REDIS_SECTION_CACHE, RedisDbConstant.REDIS_BIZ_CACHE);
    }



}
