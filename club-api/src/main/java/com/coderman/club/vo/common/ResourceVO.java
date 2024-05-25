package com.coderman.club.vo.common;

import com.coderman.club.model.common.BaseModel;
import com.coderman.club.vo.carouse.CarouseVO;
import com.coderman.club.vo.section.SectionVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResourceVO extends BaseModel {

    @ApiModelProperty(value = "分类数据")
    private List<SectionVO> sectionList;

    @ApiModelProperty(value = "轮播图")
    private List<CarouseVO> carouseList;


}
