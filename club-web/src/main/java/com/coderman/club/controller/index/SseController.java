package com.coderman.club.controller.index;

import com.coderman.club.annotation.RateLimit;
import com.coderman.club.limiter.LimiterStrategy;
import com.coderman.club.service.openai.OpenAiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author zhangyukang
 */
@Slf4j
@Api(value = "社区SSE模块", tags = {"社区SSE模块"})
@RestController
@RequestMapping(value = "/api/sse")
public class SseController {

    @Resource
    private OpenAiService openAiService;

    @ApiOperation(value = "AI聊天接口")
    @GetMapping(value = "/chatgpt", produces = "text/event-stream;charset=UTF-8")
    @RateLimit(strategy = LimiterStrategy.FIXED_WINDOW, windowSize = 30 , windowRequests = 5)
    public SseEmitter subscript(String content) {
        SseEmitter sseEmitter = new SseEmitter(-1L);

        openAiService.chatGptSse(sseEmitter, Arrays.asList(
                "你的回答控制在100个字符之内",
                "你现在是一个社区网站的AI助手"
                , content));

        return sseEmitter;
    }
}
