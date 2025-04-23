package com.alibaba.cloud.ai.example.rag.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("org.springframework.ai.chat.messages.AssistantMessage")
@JsonIgnoreProperties(ignoreUnknown = true) // 关键修复
public abstract class AssistantMessageMixin {
    @JsonCreator
    public AssistantMessageMixin(@JsonProperty("content") String content) {}
}
