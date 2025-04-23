package com.alibaba.cloud.ai.example.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RestController
@RequestMapping("/ai/v1")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public ChatController(ChatClient.Builder builder) {
        this.chatMemory = new InMemoryChatMemory();
        this.chatClient = builder
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }

    @GetMapping("/sql")
    public ModelAndView sqlPage() {
        ModelAndView modelAndView = new ModelAndView("sql"); // 跳转到page2.html
        return modelAndView;
    }



    @GetMapping(value = "/chat")
    public String chat(@RequestParam String userId,  @RequestParam String input) {

        log.info("/chat   input:  [{}]", input);

        String text =  chatClient.prompt()
                .user(input)
                .advisors(spec -> spec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, userId)
                        // 继续在 Lambda 表达式中调用 .param 方法，设置聊天记忆的检索大小为 100
                        .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call()
                .content();
        log.info("text --> [{}]", text);
        return text;
    }

}
