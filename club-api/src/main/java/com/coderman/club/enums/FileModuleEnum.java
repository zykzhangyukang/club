package com.coderman.club.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author ：zhangyukang
 * @date ：2023/09/27 10:57
 */
public enum FileModuleEnum {

    /**
     * 公共模块
     */
    COMMON_MODULE("common", "公共模块"),

    /**
     * 帖子模块
     */
    POST_MODULE("post" , "帖子模块"),

    /**
     * 用户模块
     */
    USER_MODULE("user" , "用户模块")
    ;


    /**
     * 根据code获取枚举对象
     *
     * @param code code值
     * @return
     */
    public static FileModuleEnum codeOf(String code){
        for (FileModuleEnum value : FileModuleEnum.values()) {
            if(StringUtils.equals(value.getCode() , code)){
                return value;
            }
        }
        return null;
    }

    private String code;

    private String desc;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    FileModuleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
