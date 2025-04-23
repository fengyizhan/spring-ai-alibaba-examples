package com.alibaba.cloud.ai.example.rag.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

//@Component
public class RedisChatMemory implements ChatMemory {

    private static final String KEY_PREFIX = "chat:history:";
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisChatMemory(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        String key = KEY_PREFIX + conversationId;
        redisTemplate.opsForList().rightPushAll(key, messages.toArray());
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        String key = KEY_PREFIX + conversationId;
        Long size = redisTemplate.opsForList().size(key);
        if (size == null || size == 0) return Collections.emptyList();

        int start = Math.max(0, (int) (size - lastN));
        // 获取列表最后lastN个元素（包含边界处理）
        List<Object> messages = redisTemplate.opsForList().range(key, start, -1);
        // 安全转换并过滤null值
        return messages.stream()
                .filter(obj -> obj instanceof Message)  // 类型安全检查
                .map(obj -> (Message) obj)  // 强制类型转换
                .toList();
    }

    @Override
    public void clear(String conversationId) {
        redisTemplate.delete(KEY_PREFIX + conversationId);
    }
}
