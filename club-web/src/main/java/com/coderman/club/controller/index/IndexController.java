package com.coderman.club.controller.index;

import com.coderman.club.service.carouse.CarouseService;
import com.coderman.club.service.section.SectionService;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.vo.carouse.CarouseVO;
import com.coderman.club.vo.common.ResourceVO;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.section.SectionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ：zhangyukang
 * @date ：2023/11/29 18:22
 */
@Api(value = "社区首页模块", tags = {"社区首页模块"})
@RestController
@RequestMapping(value = "/api/index")
public class IndexController {

    @Resource
    private SectionService sectionService;

    @Resource
    private CarouseService carouseService;

    @ApiOperation(value = "首页资源")
    @GetMapping(value = "")
    public ResultVO<ResourceVO> index() {

        ResourceVO resourceVO = new ResourceVO();
        // 分类数据
        resourceVO.setSectionList(this.sectionService.getSectionVoCacheList().getResult());
        // 轮播图数据
        resourceVO.setCarouseList(this.carouseService.getCarouselVoCacheList().getResult());

        return ResultUtil.getSuccess(ResourceVO.class, resourceVO);
    }

    @ApiOperation(value = "板块列表获取")
    @GetMapping(value = "/sections")
    public ResultVO<List<SectionVO>> getSections() {
        return this.sectionService.getSectionVoCacheList();
    }

    @ApiOperation(value = "轮播图列表获取")
    @GetMapping(value = "/carousels")
    public ResultVO<List<CarouseVO>> getCarousels() {
        return this.carouseService.getCarouselVoCacheList();
    }
}
