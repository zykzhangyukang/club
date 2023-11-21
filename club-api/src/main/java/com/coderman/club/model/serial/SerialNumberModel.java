package com.coderman.club.model.serial;

import com.coderman.club.model.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
@ApiModel(value="SerialNumberModel", description = "")
public class SerialNumberModel extends BaseModel {
    
    @ApiModelProperty(value = "")
    private String serialType;

    @ApiModelProperty(value = "")
    private String serialPrefix;

    @ApiModelProperty(value = "")
    private Boolean isYmd;

    @ApiModelProperty(value = "")
    private Integer digitWith;

    @ApiModelProperty(value = "")
    private Integer nextSeq;

    @ApiModelProperty(value = "")
    private Integer bufferStep;

    @ApiModelProperty(value = "")
    private Date updateTime;

    @ApiModelProperty(value = "")
    private Date cTime;

    @ApiModelProperty(value = "")
    private Date uTime;
}