package com.coderman.club.service.openai;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * openAI服务
 */
public interface OpenAiService {

    /**
     * chatgpt接口
     * @param sseEmitter
     * @param contents
     */
    public void chatGptSse(SseEmitter sseEmitter , List<String > contents);
}
