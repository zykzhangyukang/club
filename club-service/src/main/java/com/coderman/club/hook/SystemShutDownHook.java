package com.coderman.club.hook;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author coderman
 * @date 2022/8/7 19:46
 */
@Slf4j
@Component
public class SystemShutDownHook {

    @EventListener(classes = {ContextClosedEvent.class})
    public void onApplicationClosed(@NonNull ApplicationEvent event) {
        log.info("应用系统已关闭，Bye~");
    }
}