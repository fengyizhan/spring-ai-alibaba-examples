package com.hd.ai.rag.config;

import org.springframework.ai.chat.messages.UserMessage;

public class AiMessage extends UserMessage {
    public AiMessage(String content) {
        super(content);
    }
}
