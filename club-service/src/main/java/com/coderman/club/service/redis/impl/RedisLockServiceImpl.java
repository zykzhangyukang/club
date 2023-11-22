package com.coderman.club.service.redis.impl;

import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.service.redis.RedisLockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@SuppressWarnings("all")
public class RedisLockServiceImpl implements RedisLockService {


    @Resource
    private RedisTemplate redisTemplate;


    @Override
    public boolean tryLock(String lockName, long expire, long timeout) {

        boolean success = false;


        try {

            String redisLockName = RedisKeyConstant.REDIS_LOCK_PREFIX + lockName;
            ValueOperations valueOperations = redisTemplate.opsForValue();
            long beginTime = System.currentTimeMillis();


            while (true) {

                // 判断获取锁是否超时,超时则退出
                if (System.currentTimeMillis() - beginTime > timeout) {

                    log.info("[tryLock-1],超时未获取到锁");
                    break;
                }

                try {

                    // 设置lock的有效期
                    long lockValue = getTime() + expire + 1;
                    success = valueOperations.setIfAbsent(redisLockName, lockValue);

                    // 获取锁未成功
                    if (!success) {

                        // 获取锁的lock值
                        Object oldValue = valueOperations.get(redisLockName);

                        // 当前锁已过期
                        if (null != oldValue && Long.valueOf(oldValue.toString()).longValue() < getTime()) {

                            // 给lock设置一个新值,并且获取lock的原始值 (该处会有风险,如果有大量的连续线程未获取锁,但是修改了lock值,会导致lock无限延长)
                            // 因此在最后给lock添加了过期时间
                            Object getValue = valueOperations.getAndSet(redisLockName, lockValue);
                            if (null != getValue && Long.valueOf(oldValue.toString()).longValue() == Long.valueOf(getValue.toString()).longValue()) {


                                success = true;
                                log.info("[tryLock-2]锁未被他人获取过,获取锁成功");
                            } else {
                                success = false;
                                log.info("[tryLock-2]锁已被他人获取,获取锁失败");

                            }

                        } else {
                            success = false;
                            log.info("[tryLock-2]当前锁还未超时,获取锁失败");

                        }

                    }

                    // 未获取到锁,重试
                    if (!success) {

                        Thread.sleep(10);
                        log.info("[tryLock-3]未获取到锁,待重试");
                    } else {

                        redisTemplate.expire(redisLockName, lockValue, TimeUnit.MILLISECONDS);
                        log.info("[tryLock-3]获取到锁成功");
                        break;
                    }

                } catch (Exception e) {
                    log.error("[tryLock-4]循环获取lock异常:" + e.getMessage(), e);
                }

            }

        } catch (Exception e) {

            log.error("[tryLock-5]异常:" + e.getMessage(), e);
        }

        log.info("[tryLock-6]获取到锁结果:" + success);
        return success;
    }


    @Override
    public void unlock(String lockName) {


        try {
            String redisLockName = RedisKeyConstant.REDIS_LOCK_PREFIX + lockName;

            // 获取锁lock的值,如果lock中的值大于系统时间,则说明当前这个锁还是自己的锁 (只要lock的值还在有效期内,别人是无法拿到你的锁),则删除该锁资源
            Object lockValue = redisTemplate.opsForValue().get(redisLockName);
            if (null != lockValue && getTime() < Long.valueOf(lockValue.toString()).longValue()) {

                redisTemplate.delete(redisLockName);
            }
        } catch (Exception e) {

            log.error("unLock异常", e);
        }

    }


    private long getTime() {

        try {
            Object obj = redisTemplate.execute(new RedisCallback() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    return connection.time();
                }
            });

            if (obj != null) {

                return Long.valueOf(obj.toString()).longValue();
            }

        } catch (Exception e) {

            log.error(e.getMessage(), e);
        }

        return System.currentTimeMillis();
    }
}
