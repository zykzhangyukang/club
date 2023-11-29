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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ：zhangyukang
 * @date ：2023/11/29 18:27
 */
@Service
public class CarouseServiceImpl implements CarouseService {

    @Resource
    private CarouselDAO carouselDAO;

    @Resource
    private RedisService redisService;

    @Override
    public ResultVO<List<CarouseVO>> getCarouselVoCacheList() {

        List<CarouseVO> list = redisService.getListData(RedisKeyConstant.REDIS_CAROUSE_CACHE, CarouseVO.class
                , RedisDbConstant.REDIS_BIZ_CACHE);

        return ResultUtil.getSuccessList(CarouseVO.class,list);
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
