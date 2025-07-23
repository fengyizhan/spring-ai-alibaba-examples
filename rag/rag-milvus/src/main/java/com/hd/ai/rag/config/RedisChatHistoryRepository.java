package com.hd.ai.rag.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RedisChatHistoryRepository implements ChatHistoryRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final long DEFAULT_EXPIRATION = 1*86400L; // 默认24小时

    @Override
    public void append(String chatId, Message message) {
        String key = generateChatKey(chatId);
        ChatMessage chatMessage = ChatMessage.fromAIMessage(message);
        String messageId = UUID.randomUUID().toString();

        redisTemplate.opsForHash().put(key, messageId, chatMessage);
        setChatExpiration(chatId, DEFAULT_EXPIRATION);
    }

    @Override
    public List<Message> getMessages(String chatId) {
        String key = generateChatKey(chatId);
        List<Object> values = redisTemplate.opsForHash().values(key);

        return values.stream()
                .map(obj -> ((ChatMessage)obj).toAIMessage())
                .collect(Collectors.toList());
    }

    @Override
    public void clear(String chatId) {
        String key = generateChatKey(chatId);
        redisTemplate.delete(key);
    }

    @Override
    public String generateChatKey(String chatId) {
        return String.format("chat:history:%s", chatId);
    }

    @Override
    public void setChatExpiration(String chatId, long expirationSeconds) {
        String key = generateChatKey(chatId);
        redisTemplate.expire(key, expirationSeconds, TimeUnit.SECONDS);
    }
}
