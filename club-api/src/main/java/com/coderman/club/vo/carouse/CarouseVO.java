package com.coderman.club.vo.carouse;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ：zhangyukang
 * @date ：2023/11/29 18:29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CarouseVO extends BaseModel {


    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "描述信息")
    private String description;

    @ApiModelProperty(value = "图片url")
    private String imageUrl;

    @ApiModelProperty(value = "跳转url")
    private String targetUrl;

}
