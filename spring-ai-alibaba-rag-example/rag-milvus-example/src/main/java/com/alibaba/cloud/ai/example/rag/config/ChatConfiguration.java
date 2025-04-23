package com.alibaba.cloud.ai.example.rag.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ChatConfiguration {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem("你好，我是工作小助手")
                .build();
    }

    @Bean  // 声明为 Spring 容器管理的 Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
