package com.hd.ai.rag.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage implements Serializable {
    private String role;      // 角色：user, assistant
    private String content;   // 消息内容
    private long timestamp;   // 时间戳

    // 转换为Spring AI的Message对象
    public Message toAIMessage() {
        if ("user".equals(role)) {
            return new UserMessage(content);
        }
        else if ("system".equals(role)) {
            return new SystemMessage(content);
        }
        else if ("assistant".equals(role)) {
            return new AssistantMessage(content);
        }
        else {
            return new AiMessage(content);
        }
    }

    // 从Spring AI的Message对象创建
    public static ChatMessage fromAIMessage(Message message) {
        String role = "assistant";
        if (message instanceof UserMessage) {
            role = "user";
        }
        if (message instanceof SystemMessage) {
            role = "system";
        }
        if (message instanceof AssistantMessage) {
            role = "assistant";
        }
        return new ChatMessage(role, message.getText(), System.currentTimeMillis());
    }
}
