package com.hd.ai.rag.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.MetadataMode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ChatConfiguration {

    @Bean
    ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("你好，我是工作小助手")
                .build();
    }

    @Bean  // 声明为 Spring 容器管理的 Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean("mcpChatModel")
    public DashScopeChatModel mcpChatModel()
    {
        DashScopeApi dashScopeApi = new DashScopeApi(System.getenv("DASHSCOPE_API_KEY"));
        DashScopeChatModel mcpChatModel = new DashScopeChatModel(dashScopeApi,
                DashScopeChatOptions.builder()
                        .withModel("qwen-plus-latest")
                        .build());
        return mcpChatModel;
    }
    @Bean("coderChatModel")
    public DashScopeChatModel coderChatModel()
    {
        DashScopeApi dashScopeApi = new DashScopeApi(System.getenv("DASHSCOPE_API_KEY"));
        DashScopeChatModel coderChatModel = new DashScopeChatModel(dashScopeApi,
                DashScopeChatOptions.builder()
                        .withModel("qwen2.5-coder-32b-instruct")
                        .build());
        return coderChatModel;
    }

    @Primary
    @Bean("chatModel")
    public DashScopeChatModel chatModel()
    {
        DashScopeApi dashScopeApi = new DashScopeApi(System.getenv("DASHSCOPE_API_KEY"));
        DashScopeChatModel chatModel = new DashScopeChatModel(dashScopeApi,
                DashScopeChatOptions.builder()
                        .withModel("deepseek-r1")
//                        .withEnableSearch(true)
                        //联网查询
                        .build());
        return chatModel;
    }


}
