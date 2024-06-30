package com.coderman.club.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangyukang
 */
@Setter
@Getter
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


}
