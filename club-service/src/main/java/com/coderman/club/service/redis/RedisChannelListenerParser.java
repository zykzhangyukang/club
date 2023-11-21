package com.coderman.club.service.redis;

import com.coderman.club.annotation.RedisChannelListener;
import lombok.Data;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * @author zhangyukang
 * RedisChannelListener 的解析器自动注册
 */
@Component
public class RedisChannelListenerParser implements BeanPostProcessor {

    private static final CopyOnWriteArraySet<RedisListenerMetaData> CACHE_LIST = new CopyOnWriteArraySet<>();

    private Collection<RedisChannelListener> findListenerAnnotations(AnnotatedElement element) {
        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY)
                .stream(RedisChannelListener.class)
                .map(MergedAnnotation::synthesize)
                .collect(Collectors.toList());
    }


    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean,@NonNull String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);

        ReflectionUtils.doWithMethods(targetClass, method -> {
            Collection<RedisChannelListener> listenerAnnotations = findListenerAnnotations(method);
            if (!listenerAnnotations.isEmpty()) {
                RedisListenerMetaData metaData = new RedisListenerMetaData();
                metaData.setBean(bean);
                metaData.setMethod(method);
                metaData.setListeners(listenerAnnotations.toArray(new RedisChannelListener[0]));
                CACHE_LIST.add(metaData);
            }
        }, ReflectionUtils.USER_DECLARED_METHODS);
        return bean;
    }


    public CopyOnWriteArraySet<RedisListenerMetaData> getCacheList() {
        return CACHE_LIST;
    }


    @Data
    public static class RedisListenerMetaData {
        private Object bean;
        private Method method;
        private RedisChannelListener[] listeners;
    }
}

