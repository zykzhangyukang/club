package com.coderman.club.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangyukang
 */
@ConfigurationProperties(prefix = "ai.chatgpt")
@Configuration
public class ChatGptProperties {

    /**
     * openAi接口
     */
    private  String openAiUrl;
    /**
     * openAi秘钥
     */
    private  String openAiKey;


    public String getOpenAiUrl() {
        return openAiUrl;
    }

    public String getOpenAiKey() {
        return openAiKey;
    }

    public void setOpenAiUrl(String openAiUrl) {
        this.openAiUrl = openAiUrl;
    }

    public void setOpenAiKey(String openAiKey) {
        this.openAiKey = openAiKey;
    }
}
