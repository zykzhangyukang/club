package com.coderman.club.service.carouse.impl;

import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.dao.carousel.CarouselDAO;
import com.coderman.club.model.carousel.CarouselExample;
import com.coderman.club.model.carousel.CarouselModel;
import com.coderman.club.model.section.SectionExample;
import com.coderman.club.model.section.SectionModel;
import com.coderman.club.service.carouse.CarouseService;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.vo.carouse.CarouseVO;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.section.SectionVO;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author ：zhangyukang
 * @date ：2023/11/29 18:27
 */
@Service
@Slf4j
public class CarouseServiceImpl implements CarouseService {

    @Resource
    private CarouselDAO carouselDAO;

    @Resource
    private RedisService redisService;


    /**
     * 保存栏目缓存 (缓存1分钟防止频繁请求redis)
     */
    public static final Cache<String, List<CarouseVO>> CAROUSE_CACHE_MAP = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .maximumSize(500)
            .concurrencyLevel(5)
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .recordStats()
            .build();

    @Override
    public ResultVO<List<CarouseVO>> getCarouselVoCacheList() {
        List<CarouseVO> sectionVos = new ArrayList<>();
        try {
            sectionVos = CAROUSE_CACHE_MAP.get(RedisKeyConstant.REDIS_CAROUSE_CACHE, new Callable<List<CarouseVO>>() {
                @Override
                public List<CarouseVO> call() {
                    return redisService.getListData(RedisKeyConstant.REDIS_CAROUSE_CACHE, CarouseVO.class
                            , RedisDbConstant.REDIS_BIZ_CACHE);
                }
            });
        } catch (ExecutionException e) {
            log.error("获取首页轮播图失败:{}", e.getMessage(), e);
        }
        return ResultUtil.getSuccessList(CarouseVO.class, sectionVos);
    }

    @Override
    public List<CarouseVO> getCarouselVoList() {

        CarouselExample example = new CarouselExample();
        example.createCriteria().andIsActiveEqualTo(Boolean.TRUE);

        return this.carouselDAO.selectByExample(example).stream().sorted(new Comparator<CarouselModel>() {
            @Override
            public int compare(CarouselModel o1, CarouselModel o2) {
                return o1.getOrderPriority() - o2.getOrderPriority();
            }
        }).map(e -> {

            CarouseVO carouseVO = new CarouseVO();
            BeanUtils.copyProperties(e, carouseVO);
            return carouseVO;
        }).collect(Collectors.toList());
    }
}
