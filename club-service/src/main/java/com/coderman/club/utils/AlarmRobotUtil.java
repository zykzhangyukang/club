package com.coderman.club.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.concurrent.*;

/**
 * @author ：zhangyukang
 * @date ：2024/07/15 10:03
 */
@Slf4j
public class AlarmRobotUtil {

    /**
     * 发送消息线程池，最大任务队列容量为1024 (超过队列容量时丢弃任务,不抛出异常)
     */
    private static final int MAX_QUEUE_CAPACITY = 1024;
    private static final ExecutorService executorService = new ThreadPoolExecutor(
            1,
            1,
            0L,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(MAX_QUEUE_CAPACITY),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardPolicy()
    );

    /**
     * 记入报错的时间 （在30分钟内，如果出现同样的错误不需要告警）
     */
    private static final ConcurrentHashMap<String, Long> ALERT_CACHE = new ConcurrentHashMap<>();
    private static final long CACHE_DURATION = TimeUnit.MINUTES.toMillis(30);
    /**
     * 企业微信机器人配置
     */
    private static final String WEBHOOK_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=42822783-8c21-49dd-9da2-44542a23f60e";


    /**
     * 发送告警信息
     *
     * @param message 错误消息
     */
    private static void sendAlert(String message) {
        String json = "{\n" +
                "  \"msgtype\": \"text\",\n" +
                "  \"text\": {\n" +
                "    \"content\": \"" + message + "\"\n" +
                "  }\n" +
                "}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(WEBHOOK_URL)
                .post(body)
                .build();
        OkHttpClient client = OkHttpUtil.getInstance();

        log.error(message);

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                log.info("企业微信告警机器人，Alert sent successfully: {}", response.toString());
            } else {
                log.info("企业微信告警机器人，Failed to send alert: {}", response.code());
            }
        } catch (Exception e) {
            log.info("发送企业微信信息错误：{}", e.getMessage(), e);
        }
    }

    /**
     * 发送异常信息
     * @param e
     */
    public static void sendAlertWithError(Exception e, String apiEndpoint) {
        Environment environment = SpringContextUtil.getApplicationContext().getEnvironment();
        if(!Arrays.asList(environment.getActiveProfiles()).contains("pro")){
            log.warn("非生产环境，不发送告警");
            return;
        }

        String errorMessage = e.getMessage();
        String exceptionType = e.getClass().getName();
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace.length == 0) {
            return;
        }
        StackTraceElement element = stackTrace[0];
        String className = element.getClassName();
        String methodName = element.getMethodName();
        String fileName = element.getFileName();
        int lineNumber = element.getLineNumber();

        String alertKey = exceptionType + "|" + apiEndpoint + "|" + className + "|" + methodName + "|" + fileName + "|" + lineNumber;

        // 清理过期缓存
        cleanUpCache();

        // 检查是否在5分钟内已经发送过相同的告警
        long currentTime = System.currentTimeMillis();
        if (ALERT_CACHE.containsKey(alertKey) && (currentTime - ALERT_CACHE.get(alertKey)) < CACHE_DURATION) {
            log.warn("重复告警，在30分钟内不重复发送: {}" , alertKey);
            return;
        }

        // 更新缓存中的告警时间
        ALERT_CACHE.put(alertKey, currentTime);

        String messageBuilder =
                "异常类型: " + exceptionType + "\n" +
                        "错误信息: " + errorMessage + "\n" +
                        "接口信息: " + apiEndpoint + "\n" +
                        "发生位置: " + className + "." + methodName +
                        "(" + fileName + ":" + lineNumber + ")\n";

        executorService.submit(() -> sendAlert(messageBuilder));
    }

    private static void cleanUpCache() {
        long currentTime = System.currentTimeMillis();
        ALERT_CACHE.entrySet().removeIf(entry -> (currentTime - entry.getValue()) >= CACHE_DURATION);
    }
}
