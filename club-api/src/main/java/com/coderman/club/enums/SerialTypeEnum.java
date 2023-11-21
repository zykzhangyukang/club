package com.coderman.club.enums;

/**
 * @author coderman
 * @Title: 序列号类型
 * @date 2022/6/2119:45
 */
public enum SerialTypeEnum {

    ORDER("order", "订单编号"),
    EMP_CODE("emp_code","员工工号");


    private String key;
    private String desc;

    SerialTypeEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}