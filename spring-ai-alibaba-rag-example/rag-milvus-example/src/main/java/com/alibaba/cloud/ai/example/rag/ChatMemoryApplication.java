package com.alibaba.cloud.ai.example.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;


@SpringBootApplication
@MapperScan("com.alibaba.cloud.ai.example.rag.mapper")
public class ChatMemoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatMemoryApplication.class, args);
    }

}
