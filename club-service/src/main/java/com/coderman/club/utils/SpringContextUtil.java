package com.coderman.club.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * @author coderman
 * @date 2022/6/2120:12
 */
@Slf4j
@Service
@Order(Integer.MIN_VALUE)
@Lazy(value = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.ANY)
public class SpringContextUtil implements ApplicationContextAware, DisposableBean {

    @Getter
    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext){
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) throws BeansException{
        checkApplicationContext();
        return (T) applicationContext.getBean(clazz);
    }


    @SuppressWarnings("all")
    public static <T> T getBean(String beanName){
        checkApplicationContext();
        return (T) applicationContext.getBean(beanName);
    }


    /**
     * 发布事件
     * @param event
     */
    public static void publishEvent(ApplicationEvent event){
        checkApplicationContext();
        applicationContext.publishEvent(event);
    }


    private static void checkApplicationContext(){

        if(applicationContext == null){
            throw new IllegalStateException("applicationContext未注入!");
        }
    }

    @Override
    public void destroy() throws Exception {

    }
}
