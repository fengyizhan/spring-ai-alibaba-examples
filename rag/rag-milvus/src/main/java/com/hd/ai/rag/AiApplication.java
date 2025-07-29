package com.hd.ai.rag;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.http.client.HttpClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;


@MapperScan("com.hd.ai.rag.mapper")
// 排除所有可能冲突的自动配置类
@SpringBootApplication(exclude = {
        HttpClientAutoConfiguration.class,    // 排除HttpClient自动配置
        RestTemplateAutoConfiguration.class,   // 排除RestTemplate自动配置
        RestClientAutoConfiguration.class,       //排除RestClient的自动配置
        HttpMessageConvertersAutoConfiguration.class // 排除可能间接依赖的消息转换器配置
})
public class AiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiApplication.class, args);

    }

}
