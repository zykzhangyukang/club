package com.coderman.club.hook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Date;

/**
 * @author coderman
 * @date 2022/8/7 19:48
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SystemStartedUpRunner implements ApplicationRunner {

    private final ConfigurableApplicationContext context;

    @Value("${server.port:8080}")
    private String port;
    @Value("${server.servlet.context-path:}")
    private String contextPath;
    @Value("${spring.profiles.active:}")
    private String profilesActive;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (context.isActive()) {
            InetAddress address = InetAddress.getLocalHost();
            String url = String.format("http://%s:%s", address.getHostAddress(), port);
            if (StringUtils.isNotBlank(contextPath)) {
                url += contextPath;
            }

            log.info("应用系统启动完毕，当前系统时间：{}，地址：{} , env: {}", DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"), url , profilesActive);
        }
    }
}
