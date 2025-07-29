package com.hd.ai.rag.controller;

import com.hd.ai.rag.tools.TickTool;
import com.hd.ai.rag.tools.TimeTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RestController
@RequestMapping("/ai/v1")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatClient chatClient;
    private final ChatClient mcpClient;
    private final ChatMemory chatMemory;

    public ChatController(ChatClient.Builder builder,@Qualifier("mcpChatModel") ChatModel mcpChatModel) {
        this.chatMemory = new InMemoryChatMemory();
        MessageChatMemoryAdvisor mma=new MessageChatMemoryAdvisor(chatMemory);
        this.chatClient = builder
                .defaultAdvisors(mma)
                .build();
        this.mcpClient = ChatClient.builder(mcpChatModel)
                .defaultAdvisors(mma)
                .build();

    }

    @GetMapping("/sql")
    public ModelAndView sqlPage() {
        ModelAndView modelAndView = new ModelAndView("sql"); // 跳转到page2.html
        return modelAndView;
    }



    @GetMapping(value = "/chat")
    public String chat(@RequestParam String userId,  @RequestParam String input,@RequestParam Boolean useTools) {

        log.info("/chat   input:  [{}]", input);
        String text="";
        if(useTools)
        {//如果使用工具，那么调用大模型时要动态设置请求模型参数
            text =  mcpClient
                    .prompt()
                    .user(input)
                    .advisors(spec -> spec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, userId)
                            // 继续在 Lambda 表达式中调用 .param 方法，设置聊天记忆的检索大小为 1000
                            .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 1000))
                    .tools(new TimeTool(),new TickTool())
                    .call()
                    .content();
            log.info("toolcall text --> [{}]", text);
        }
        else
        {
            text =  chatClient
                    .prompt()
                    .user(input)
                    .advisors(spec -> spec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, userId)
                            // 继续在 Lambda 表达式中调用 .param 方法，设置聊天记忆的检索大小为 1000
                            .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 1000))
                    .call()
                    .content();
            log.info("common text --> [{}]", text);
        }
        return text;
    }

}
