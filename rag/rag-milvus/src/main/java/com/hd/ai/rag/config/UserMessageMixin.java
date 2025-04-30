package com.hd.ai.rag.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("org.springframework.ai.chat.messages.UserMessage")  // 绑定类型标识
@JsonIgnoreProperties(ignoreUnknown = true)  // 忽略未知字段（关键修复点）
public abstract class UserMessageMixin {
    @JsonCreator  // 标注构造器反序列化方式
    public UserMessageMixin(@JsonProperty("content") String content) {}  // 显式绑定content字段
}
