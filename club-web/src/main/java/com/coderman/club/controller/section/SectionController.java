package com.coderman.club.controller.section;

import com.coderman.club.service.section.SectionService;
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
 * @date ：2023/11/24 14:25
 */
@Api(value = "社区板块模块", tags = {"社区板块模块"})
@RestController
@RequestMapping(value = "/api/section")
public class SectionController {

    @Resource
    private SectionService sectionService;

    @ApiOperation(value = "板块列表获取")
    @GetMapping(value = "/list")
    public ResultVO<List<SectionVO>> list() {

        return this.sectionService.list();
    }
}
