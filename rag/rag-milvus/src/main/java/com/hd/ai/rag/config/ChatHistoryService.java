package com.hd.ai.rag.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatHistoryService {

    private final RedisChatHistoryRepository repository;

    // 添加新消息
    public void addMessage(String chatId, String role, String content) {
        Message message;
        if ("user".equals(role)) {
            message = new UserMessage(content);
        }
        else if ("assistant".equals(role)) {
            message = new AssistantMessage(content);
        }
        else if ("system".equals(role)) {
            message = new SystemMessage(content);
        }
        else {
            message = new AiMessage(content);
        }
        repository.append(chatId, message);
    }

    // 获取指定会话的所有消息
    public List<Message> getConversation(String chatId) {
        return repository.getMessages(chatId);
    }

    // 删除会话
    public void deleteConversation(String chatId) {
        repository.clear(chatId);
    }

    // 获取最近n条消息
    public List<Message> getLastMessages(String chatId, int count) {
        List<Message> allMessages = repository.getMessages(chatId);
        int startIndex = Math.max(0, allMessages.size() - count);
        return allMessages.subList(startIndex, allMessages.size());
    }
}
