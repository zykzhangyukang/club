package com.coderman.club.service.serial;


import com.coderman.club.enums.SerialTypeEnum;

import java.util.List;

/**
 * @author coderman
 * @date 2022/6/2119:50
 */
public interface SerialNumberService {


    /**
     * 获取id
     * @param serialTypeEnum
     * @param serialCount
     * @return
     */
    public List<String> noTranGetSerialNumberList(SerialTypeEnum serialTypeEnum, Integer serialCount);


    /**
     * 清空缓存区
     */
    public void clearSerialBufferMap();

}
