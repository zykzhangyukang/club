package com.coderman.club.annotation;

import java.lang.annotation.*;

/**
 * @author zhangyukang
 * redis 发布定义监听者注解 用于标注在方法上 省去多余的注册操作
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisChannelListener {


    /**
     * 实际通道名
     */
    String channelName() default "";


    /**
     * 接受的消息实体类名
     */
    Class<?> clazz() default String.class;
}

