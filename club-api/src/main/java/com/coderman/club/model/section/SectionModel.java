package com.coderman.club.model.section;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This is the base record class for table: club_section
 * Generated by MyBatis Generator.
 * @author MyBatis Generator
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value="SectionModel", description = "club_section 实体类")
public class SectionModel extends BaseModel {
    

    @ApiModelProperty(value = "主键id")
    private Long sectionId;

    @ApiModelProperty(value = "版块名称")
    private String sectionName;

    @ApiModelProperty(value = "父版块（可为空）")
    private Long parentSection;

    @ApiModelProperty(value = "版块描述")
    private String description;

    @ApiModelProperty(value = "是否处于启用状态")
    private Boolean isActive;

    @ApiModelProperty(value = "排序字段")
    private Integer sort;
}