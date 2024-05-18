package com.coderman.club.init;

import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Administrator
 */
@Lazy(value = false)
@Component
public class CacheLifecycle implements SmartLifecycle {

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    @Resource
    private IndexCacheInitializer indexCacheInitializer;

    @Override
    public void start() {

        if (this.initialized.compareAndSet(false, true)) {

            // 初始化首页缓存数据
            this.indexCacheInitializer.init();
        }

    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop() {
        this.initialized.getAndSet(false);
    }

    @Override
    public void stop(Runnable callback) {
        callback.run();
    }

    @Override
    public boolean isRunning() {
        return this.initialized.get();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}