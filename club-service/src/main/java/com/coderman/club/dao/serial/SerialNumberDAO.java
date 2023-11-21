package com.coderman.club.dao.serial;


import com.coderman.club.dao.common.BaseDAO;
import com.coderman.club.model.serial.SerialNumberExample;
import com.coderman.club.model.serial.SerialNumberModel;

import java.util.Map;

public interface SerialNumberDAO extends BaseDAO<SerialNumberModel, SerialNumberExample> {

    /**
     * 更新并且获取序列号
     *
     * @param paramMap
     */
    void getSerialNumber(Map<String, Object> paramMap);
}