package com.alibaba.cloud.ai.example.rag.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 - @des Milvus向量数据库配置
 */
@Configuration
public class MilvusServiceConfig {

    @Value("${milvus.host}")
    private String host;
    @Value("${milvus.port}")
    private Integer port;
    @Value("${milvus.username}")
    private String username;
    @Value("${milvus.password}")
    private String password;

    @Bean
    public MilvusClientV2 milvusClientV2() {
        String uri = "http://" + this.host + ":" + this.port.toString();
        return new MilvusClientV2(ConnectConfig.builder().uri(uri).username(username).password(password).build());
    }

//    @Bean
//    public MilvusServiceClient milvusServiceClient() {
//        ConnectParam connectParam = ConnectParam.newBuilder()
//                .withHost(host)
//                .withPort(port)
//                .withAuthorization(username,password)
//                .build();
//        return new MilvusServiceClient(connectParam);
//    }
}
