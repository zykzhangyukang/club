package com.coderman.club.service.carouse;

import com.coderman.club.vo.carouse.CarouseVO;
import com.coderman.club.vo.common.ResultVO;

import java.util.List;

/**
 * @author zhangyukang
 */
public interface CarouseService {

    /**
     * 轮播图列表获取
     * @return
     */
    ResultVO<List<CarouseVO>> getCarouselVoCacheList();

    /**
     * 轮播图列表获取
     * @return
     */
    List<CarouseVO> getCarouselVoList();
}
