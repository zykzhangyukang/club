package com.coderman.club.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * @author ：zhangyukang
 * @date ：2023/11/24 16:45
 */
public class DelayQueueUtil {

    private static final Map<String, Consumer<?>> CONSUMER_MAP = new ConcurrentHashMap<>();

    private static final AtomicBoolean STARTING = new AtomicBoolean();

    /**
     * 延迟队列
     */
    private static final DelayQueue<DelayMessage<?>> DELAY_QUEUE = new DelayQueue<>();

    private static final int CORE_POOL_SIZE = 2;
    private static final int MAXIMUM_POOL_SIZE = 4;
    private static final long KEEP_ALIVE_TIME = 20;
    private static final TimeUnit UNIT = TimeUnit.SECONDS;
    private static final int MAXIMUM_ARRAY_SIZE = 1024;
    private static final ThreadFactory NAMED_FACTORY = new ThreadFactoryBuilder().setNameFormat("java_delay_thread_%d").build();

    /**
     * 执行读取任务的线程池
     */
    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            UNIT,
            new ArrayBlockingQueue<>(MAXIMUM_ARRAY_SIZE),
            NAMED_FACTORY);


    /**
     * 提交一个延迟消息
     * @param uuid 消息的uuid
     * @param msg 消息对象
     * @param consumer 延迟到期后回到方法
     * @param delayTime 延迟时间，毫秒
     * @param <T> 消息对象类型
     * @return true: 提交成功
     */
    public static <T> boolean submit(String uuid, T msg, Consumer<T> consumer, long delayTime) {
        DelayMessage<T> delayMessage = new DelayMessage<>(uuid, msg, delayTime);
        addTask(uuid, consumer);

        return DELAY_QUEUE.offer(delayMessage);
    }

    /**
     * 取消一个延迟消息
     * @param uuid 消息的uuid
     * @return true: 取消成功
     */
    public static boolean cancel(String uuid) {
        return CONSUMER_MAP.remove(uuid) != null;
    }

    /**
     * 添加任务，懒加载开启消费线程
     * @param uuid 消息的uuid
     * @param consumer 回调方法
     * @param <T> 消息对象类型
     */
    @SuppressWarnings("unchecked")
    private static <T> void addTask(String uuid, Consumer<T> consumer) {
        CONSUMER_MAP.put(uuid, consumer);

        // STARTING 是false，则开启监听队列的线程
        if (!STARTING.compareAndSet(false, true)) {
            return;
        }
        THREAD_POOL.execute(() -> {
            while (STARTING.get()) {
                try {
                    DelayMessage<T> delayMessage = (DelayMessage<T>) DELAY_QUEUE.take();
                    // 只有当map里面有该uuid对应的消息，才执行回调方法
                    if (CONSUMER_MAP.containsKey(delayMessage.getUuid())) {
                        // 执行回调方法
                        execCall(consumer, delayMessage);
                    }
                } catch (InterruptedException e) {
                    STARTING.set(false);
                }
            }
        });
    }

    private static <T> void execCall(Consumer<T> consumer, DelayMessage<T> delayMessage) {
        CONSUMER_MAP.remove(delayMessage.getUuid());
        THREAD_POOL.execute(() -> consumer.accept(delayMessage.getBody()));
    }
}

class DelayMessage<T> implements Delayed {

    private static final AtomicLong ATOMIC = new AtomicLong(0);

    private final long n;

    private String uuid;

    /**
     * 消息内容
     */
    private T body;

    /**
     * 到期时间，这个是必须的属性因为要按照这个判断延时时长。
     */
    private long executeTime;

    /**
     * 延迟毫秒数
     */
    private long delayTime;

    public DelayMessage(String uuid, T body, long delayTime) {
        this.uuid = uuid;
        this.n = ATOMIC.getAndIncrement();
        this.body = body;
        this.delayTime = delayTime;
        this.executeTime = TimeUnit.NANOSECONDS.convert(delayTime, TimeUnit.MILLISECONDS) + System.nanoTime();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.executeTime - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(@NonNull Delayed other) {
        if (other == this) {
            return 0;
        }
        if (other instanceof DelayMessage) {
            DelayMessage<?> x = (DelayMessage<?>) other;
            long diff = executeTime - x.executeTime;
            if (diff < 0) {
                return -1;
            } else if (diff > 0) {
                return 1;
            } else if (n < x.n) {
                return -1;
            } else {
                return 1;
            }
        }
        long d = (getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS));
        return (d == 0) ? 0 : (d < 0 ? -1 : 1);
    }

    public static AtomicLong getAtomic() {
        return ATOMIC;
    }

    public long getN() {
        return n;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(long executeTime) {
        this.executeTime = executeTime;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }
}
