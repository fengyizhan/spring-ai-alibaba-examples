package com.hd.ai.rag.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RedisChatMemory implements ChatMemory {

    private final ChatHistoryService chatHistoryService;

    public RedisChatMemory(ChatHistoryService chatHistoryService) {
        this.chatHistoryService = chatHistoryService;
    }

    @Override
    public void add(String conversationId, Message message) {
       chatHistoryService.addMessage(conversationId,message.getMessageType().getValue(),message.getText());
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        if(messages == null || messages.isEmpty()) return;
        for(Message message : messages)
        {
            chatHistoryService.addMessage(conversationId,message.getMessageType().getValue(),message.getText());
        }
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        return List.of();
    }

    @Override
    public void clear(String conversationId) {
        chatHistoryService.deleteConversation(conversationId);
    }
}
