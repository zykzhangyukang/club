package com.coderman.club.utils;


import com.coderman.club.enums.SerialTypeEnum;
import com.coderman.club.service.serial.SerialNumberService;

import java.util.List;

/**
 * @author coderman
 * @Description: 序列工具类
 * @date 2022/6/2120:09
 */
public class SerialNumberUtil {

    private static final SerialNumberService serialNumberService;

    static {
        serialNumberService = SpringContextUtil.getApplicationContext().getBean(SerialNumberService.class);
    }


    public static String get(SerialTypeEnum serialTypeEnum) {
        return getList(serialTypeEnum, 1).get(0);
    }

    private static List<String> getList(SerialTypeEnum serialTypeEnum, int serialCount) {
        return serialNumberService.noTranGetSerialNumberList(serialTypeEnum, serialCount);
    }
}
