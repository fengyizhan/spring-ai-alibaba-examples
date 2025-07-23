package com.hd.ai.rag.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    private RedisConnectionFactory factory;


    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        this.factory = factory;
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(om.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(om);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }


//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
//        this.factory = factory;
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(factory);  // 绑定Redis连接工厂
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        // 启用多态类型支持，匹配Redis存储的WRAPPER_ARRAY格式
//        objectMapper.activateDefaultTyping(
//                LaissezFaireSubTypeValidator.instance,  // 禁用子类型验证
//                ObjectMapper.DefaultTyping.EVERYTHING,  // 对所有类型启用类型信息
//                JsonTypeInfo.As.WRAPPER_ARRAY  // 使用数组包装类型信息
//        );
//        // 全局配置：忽略未知字段（关键修复点）
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        // 注册自定义消息类型的Mixin（解决构造函数匹配问题）
//        objectMapper.addMixIn(SystemMessage.class, SystemMessageMixin.class);
//        objectMapper.addMixIn(UserMessage.class, UserMessageMixin.class);
//        objectMapper.addMixIn(AssistantMessage.class, AssistantMessageMixin.class);
//
//        Jackson2JsonRedisSerializer<Object> serializer =
//                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);  // 创建定制化序列化器
//
//        template.setKeySerializer(new StringRedisSerializer());  // 字符串序列化键
//        template.setValueSerializer(serializer);  // JSON序列化值
//        return template;
//    }
}
