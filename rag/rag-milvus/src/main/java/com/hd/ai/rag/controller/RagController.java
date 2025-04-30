package com.hd.ai.rag.controller;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/rag")
@CrossOrigin(origins = "*")
public class RagController {

    private final VectorStore vectorStore;

    private final ChatClient chatClient;

    public RagController(@Qualifier("vectorStore2") VectorStore vectorStore, ChatClient chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
    }

    // 历史消息列表
    private static List<Message> historyMessage = new ArrayList<>();

    // 历史消息列表的最大长度
    private final static int maxLen = 10;

    @GetMapping(value = "/chat")
    public Flux<String> generation(
            @RequestParam("prompt") String userInput,
            HttpServletResponse response
    ) {

        response.setCharacterEncoding("UTF-8");
        // 发起聊天请求并处理响应
        Flux<String> resp = chatClient.prompt()
                .messages(historyMessage)
                .user(userInput)
                .advisors(new QuestionAnswerAdvisor(
                        vectorStore,
                        SearchRequest.builder().build())
                ).stream()
                .content();

        // 用户输入的文本是 UserMessage
        historyMessage.add(new UserMessage(userInput));

        // 发给 AI 前对历史消息对列的长度进行检查
        if (historyMessage.size() > maxLen) {
            historyMessage = historyMessage.subList(historyMessage.size() - maxLen - 1, historyMessage.size());
        }

        return resp;
    }

    /**
     * 向量数据查询测试
     */
    @GetMapping("/select")
    public void search() {
        var dashScopeApi = new DashScopeApi(System.getenv("DASHSCOPE_API_KEY"));
        var embeddingModel = new DashScopeEmbeddingModel(dashScopeApi, MetadataMode.EMBED,
                DashScopeEmbeddingOptions.builder()
                        .withModel("text-embedding-v3")
                        .build());
        EmbeddingResponse embeddingResponse = embeddingModel
                .embedForResponse(List.of("Hello World", "World is big and salvation is near"));
    }




}
