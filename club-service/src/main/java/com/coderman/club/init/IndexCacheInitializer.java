package com.coderman.club.init;

import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.dao.carousel.CarouselDAO;
import com.coderman.club.model.carousel.CarouselModel;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 首页缓存初始化
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


        } catch (Exception e) {
            log.error("缓存数据加载失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 首页轮播图数据加载
     */
    private void initCarouseCache() {
        this.redisService.del(RedisKeyConstant.REDIS_CAROUSE_CACHE, RedisDbConstant.REDIS_BIZ_CACHE);
        List<CarouseVO> carouselVoList = this.carouseService.getCarouselVoList();
        this.redisService.setListData(RedisKeyConstant.REDIS_CAROUSE_CACHE, carouselVoList, RedisDbConstant.REDIS_BIZ_CACHE);
    }

    /**
     * 首页栏目缓存数据加载
     */
    private void initSectionCache() {
        this.redisService.del(RedisKeyConstant.REDIS_SECTION_CACHE, RedisDbConstant.REDIS_BIZ_CACHE);
        List<SectionVO> sectionVOList = this.sectionService.getSectionVoList();
        this.redisService.setListData(RedisKeyConstant.REDIS_SECTION_CACHE, sectionVOList, RedisDbConstant.REDIS_BIZ_CACHE);
    }


    /**
     * 栏目数据缓存5分钟刷新一次
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void refreshIndexCache() {
        initSectionCache();
        log.info("refreshSectionCache#首页栏目缓存数据刷新完成....");
        initCarouseCache();
        log.info("refreshCarouseCache#首页轮播图数据刷新完成....");
    }

}
