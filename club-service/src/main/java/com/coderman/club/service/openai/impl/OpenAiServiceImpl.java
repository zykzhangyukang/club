package com.coderman.club.service.openai.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coderman.club.exception.BusinessException;
import com.coderman.club.properties.ChatGptProperties;
import com.coderman.club.service.openai.OpenAiService;
import com.coderman.club.utils.OkHttpUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class OpenAiServiceImpl implements OpenAiService {


    @Resource
    private ChatGptProperties chatGptProperties;


    @Override
    public void chatGptSse(SseEmitter sseEmitter, List<String> contents) {

        // 构建POST参数
        JSONArray messages = new JSONArray();

        //JSONObject tips = new JSONObject();
        //tips.put("role", "user");
        //tips.put("content", "ChatGPT你每个回复的内容字符数，必须帮我控制在10个字符以内！");
        //messages.add(tips);

        for (String value : contents) {
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", value);
            messages.add(message);
        }

        JSONObject params = new JSONObject();
        params.put("model", "gpt-4");
        params.put("messages", messages);
        params.put("stream", true);

        // 创建请求体
        RequestBody body = RequestBody.create(params.toJSONString(), MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(chatGptProperties.getOpenAiUrl())
                .post(body)
                .addHeader("Authorization", "Bearer " + chatGptProperties.getOpenAiKey())
                .addHeader("Accept", "text/event-stream")
                .build();
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();

        StringBuffer sb = new StringBuffer();

        EventSources.createFactory(okHttpClient).newEventSource(request, new EventSourceListener() {

            @Override
            public void onOpen(@NonNull EventSource eventSource, @NonNull Response response) {
                log.info("=====> onOpen");
            }

            @SneakyThrows
            @Override
            public void onEvent(@NonNull EventSource eventSource, String id, String type, @NonNull String data) {

                try {

                    this.parseContent(data);

                    sseEmitter.send(data);
                    log.debug("onEvent==> {}", data);

                } catch (IOException e) {
                    log.error("onEvent  sseEmitter#send error:{}", e.getMessage(), e);
                }
            }

            /**
             * 解析chatgpt消息内容
             *
             * @param data
             */
            private void parseContent(@NonNull String data) {
                if ("[DONE]".equals(data)) {
                    return;
                }
                try {
                    JSONObject jsonObject = JSON.parseObject(data.replace("data:", ""));
                    String content = jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("delta").getString("content");
                    if (StringUtils.isNotBlank(content)) {
                        sb.append(content);
                    }
                } catch (Exception e) {
                    log.info("parseContent error :{}, data:{}", e.getMessage(), data);
                }
            }

            @Override
            public void onClosed(@NonNull EventSource eventSource) {
                log.info("=====> close");
                sseEmitter.complete();
                log.info("result={}", sb);
            }

            @Override
            public void onFailure(@NonNull EventSource eventSource, Throwable t, Response response) {
                log.error("连接OpenAI平台时出现错误，error:{}，response:{}", t,JSON.toJSONString(response) ,t);
                sseEmitter.completeWithError(t);
            }
        });

    }
}
