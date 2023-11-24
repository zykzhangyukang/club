package com.coderman.club.vo.section;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author ：zhangyukang
 * @date ：2023/11/24 14:28
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SectionVO extends BaseModel {

    @ApiModelProperty(value = "主键id")
    private Long sectionId;

    @ApiModelProperty(value = "版块名称")
    private String sectionName;

    @ApiModelProperty(value = "版块描述")
    private String description;

    @ApiModelProperty(value = "二级板块")
    private List<SectionVO> children;
}
