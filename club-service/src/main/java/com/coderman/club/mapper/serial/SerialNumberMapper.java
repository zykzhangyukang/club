package com.coderman.club.mapper.serial;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coderman.club.model.serial.SerialNumberModel;

import java.util.Map;

public interface SerialNumberMapper  extends BaseMapper<SerialNumberModel> {

    /**
     * 更新并且获取序列号
     *
     * @param paramMap
     */
    void getSerialNumber(Map<String, Object> paramMap);
}