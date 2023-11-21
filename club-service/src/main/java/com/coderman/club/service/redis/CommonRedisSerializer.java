package com.coderman.club.service.redis;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.Assert;

/**
 * @author ：zhangyukang
 * @date ：2023/10/17 14:03
 */
public class CommonRedisSerializer<T> implements RedisSerializer<T> {

    private Class<T> targetClazz;


    private static final GenericJackson2JsonRedisSerializer GENERIC_JACKSON2_JSON_REDIS_SERIALIZER = new GenericJackson2JsonRedisSerializer();

    private CommonRedisSerializer() {
    }

    /**
     * 序列化器
     *
     * @param targetClazz
     */
    public CommonRedisSerializer(Class<T> targetClazz) {
        Assert.notNull(targetClazz, "目标序列化类不能为空!!");
        this.targetClazz = targetClazz;
    }


    @Override
    public byte[] serialize(T t) throws SerializationException {
        return GENERIC_JACKSON2_JSON_REDIS_SERIALIZER.serialize(t);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        return GENERIC_JACKSON2_JSON_REDIS_SERIALIZER.deserialize(bytes, targetClazz);
    }
}
