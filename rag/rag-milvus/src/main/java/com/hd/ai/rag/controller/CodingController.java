package com.hd.ai.rag.controller;

import com.hd.ai.rag.entity.DemandDocument;
import com.hd.ai.rag.entity.DesignDocument;
import com.hd.ai.rag.service.DemandDocService;
import com.hd.ai.rag.service.DesignDocService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/ai/coding")
@CrossOrigin(origins = "*")
public class CodingController {

    private final ChatClient chatClient;
    private final ChatClient mcpClient;
    private final ChatMemory chatMemory;
    @Autowired
    private DesignDocService designDocService;
    @Autowired
    private DemandDocService demandDocService;

    @Value("classpath:/template/coding.txt")
    private Resource codingResource;

    public CodingController(ChatClient.Builder builder, @Qualifier("mcpChatModel") ChatModel mcpChatModel) {
        this.chatMemory = new InMemoryChatMemory();
        MessageChatMemoryAdvisor mma=new MessageChatMemoryAdvisor(chatMemory);
        this.chatClient = builder
                .defaultAdvisors(mma)
                .build();
        this.mcpClient = ChatClient.builder(mcpChatModel)
                .defaultAdvisors(mma)
                .build();

    }


    @GetMapping(value = "/coding")
    public String coding(@RequestParam String userId,@RequestParam String[] demandDocIds,@RequestParam String[] designDocIds) {

        String codingTemplate = null;
        try {
            codingTemplate = codingResource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SystemPromptTemplate systemPromptTemplate=new SystemPromptTemplate(codingTemplate);
        List<DemandDocument> demandDocuments=demandDocService.getBaseMapper().selectByIds(Arrays.asList(demandDocIds));
        StringBuffer demandDocBuffer=new StringBuffer();
        demandDocuments.forEach(demandDocument -> { demandDocBuffer.append(demandDocument.getContent()); });
        List<DesignDocument> designDocuments=designDocService.getBaseMapper().selectByIds(Arrays.asList(designDocIds));
        StringBuffer designDocBuffer=new StringBuffer();
        designDocuments.forEach(designDocument -> { designDocBuffer.append(designDocument.getContent()); });
        String finalContent=systemPromptTemplate.create(Map.of("coding_rules",demandDocBuffer.toString(),"design_docs",designDocBuffer.toString())).getContents();
        String text="";
        text =  chatClient
                .prompt()

                .user(finalContent)
                .advisors(spec -> spec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, userId)
                        // 继续在 Lambda 表达式中调用 .param 方法，设置聊天记忆的检索大小为 100
                        .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call()
                .content();
            log.info("common text --> [{}]", text);
        return text;
    }

}
