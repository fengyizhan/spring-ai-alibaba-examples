package com.hd.ai.rag.config;

import org.springframework.ai.chat.messages.Message;

import java.util.List;

public interface ChatHistoryRepository {
    void append(String chatId, Message message);

    List<Message> getMessages(String chatId);

    void clear(String chatId);

    String generateChatKey(String chatId);

    void setChatExpiration(String chatId, long expirationSeconds);
}
