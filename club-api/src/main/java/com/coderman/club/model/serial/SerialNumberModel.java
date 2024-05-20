package com.coderman.club.model.serial;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName(value = "pub_serial_number")
public class SerialNumberModel extends BaseModel {

    @TableId(value = "serial_type")
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