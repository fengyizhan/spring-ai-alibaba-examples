package com.hd.ai.rag.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http2.ssl.H2ClientTlsStrategy;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.SSLContext;

import static java.lang.System.getenv;

@Configuration
public class ChatConfiguration {

    @Bean
    ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("你好，我是工作小助手")
                .build();
    }

//    @Bean  // 声明为 Spring 容器管理的 Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }

    @Bean("mcpChatModel")
    public DashScopeChatModel mcpChatModel()
    {
        DashScopeApi dashScopeApi = new DashScopeApi(System.getenv("DASHSCOPE_API_KEY"));
        DashScopeChatModel mcpChatModel = new DashScopeChatModel(dashScopeApi,
                DashScopeChatOptions.builder()
                        .withModel("qwen-max-latest")
                    //qwen-coder-plus-latest
                        .build());
        return mcpChatModel;
    }
    @Bean("coderChatModel")
    public DashScopeChatModel coderChatModel()
    {
        DashScopeApi dashScopeApi = new DashScopeApi(System.getenv("DASHSCOPE_API_KEY"));
        DashScopeChatModel coderChatModel = new DashScopeChatModel(dashScopeApi,
                DashScopeChatOptions.builder()
                        .withModel("qwen2.5-coder-32b-instruct")
                        .build());
        return coderChatModel;
    }

    @Primary
    @Bean("chatModel")
    public DashScopeChatModel chatModel(RestClient.Builder restClientBuilder,WebClient.Builder webClientBuilder)
    {
        DashScopeApi dashScopeApi=new DashScopeApi("https://dashscope.aliyuncs.com", System.getenv("DASHSCOPE_API_KEY"), restClientBuilder, webClientBuilder, RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER);
        DashScopeChatModel chatModel = new DashScopeChatModel(dashScopeApi,
                DashScopeChatOptions.builder()
                        .withModel(
                                "qwen3-coder-plus"
                        )
                        .withTemperature(0d)
                        //支持1M的上下文长度
//                        .withEnableSearch(true)
                        //联网查询
                        .build());
        return chatModel;
    }


    @Bean
    public HttpClientConnectionManager connectionManager() {
        // 创建池化连接管理器
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        // 设置最大连接数
        connectionManager.setMaxTotal(500);
        // 设置每个路由的默认最大连接数
        connectionManager.setDefaultMaxPerRoute(200);
        // 设置连接存活时间
        connectionManager.setValidateAfterInactivity(TimeValue.ofSeconds(600));
        return connectionManager;
    }

    @Bean
    public HttpClient httpClient(HttpClientConnectionManager connectionManager) {
        // 设置请求超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(5))
                .setResponseTimeout(Timeout.ofSeconds(60))
                .build();

        // 构建HttpClient，使用连接池管理器和请求配置
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictIdleConnections(TimeValue.ofSeconds(60))
                .build();
    }



    @Bean
    public RestTemplate restTemplate(HttpClient httpClient) {
        // 创建基于HttpClient的请求工厂
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        // 设置HttpClient
        requestFactory.setHttpClient(httpClient);
        // 设置连接超时时间
        requestFactory.setConnectTimeout(5000);
        // 设置读取超时时间
        requestFactory.setReadTimeout(60000);

        // 创建RestTemplate并设置请求工厂
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(requestFactory);
        return restTemplate;
    }



    @Bean
    public CloseableHttpAsyncClient asyncHttpClient() {
        // 1. 配置IO线程池
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setSoTimeout(Timeout.ofMinutes(2))  // 套接字超时（读取数据超时）
                .setIoThreadCount(Runtime.getRuntime().availableProcessors() * 2)  // 调整IO线程数
                .build();

        // 2. 配置SSL上下文（默认信任策略）
        SSLContext sslContext = SSLContexts.createDefault();


        // 3. 配置TLS策略 (HttpClient 5.x)
        TlsStrategy tlsStrategy = new H2ClientTlsStrategy(sslContext); // 支持HTTP/2


        // 4. 配置异步连接池
        PoolingAsyncClientConnectionManager connectionManager = PoolingAsyncClientConnectionManagerBuilder.create()
                .setTlsStrategy(tlsStrategy)  // 绑定TLS策略
                .setMaxConnTotal(500)  // 总连接数
                .setMaxConnPerRoute(200)  // 单路由连接数
                .build();

        // 5. 构建异步HttpClient
        return HttpAsyncClients.custom()
                .setConnectionManager(connectionManager)
                .setIOReactorConfig(ioReactorConfig)
                .build();




    }

    @Bean
    public WebClient.Builder webClient(CloseableHttpAsyncClient asyncHttpClient) {
        // 启动异步HttpClient
        asyncHttpClient.start();

        // 使用异步HttpClient创建连接器
        HttpComponentsClientHttpConnector connector =
                new HttpComponentsClientHttpConnector(asyncHttpClient);

        return WebClient.builder()
                .clientConnector(connector)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024));
    }


    @Bean
    public RestClient.Builder restClient(RestTemplate restTemplate,HttpClientConnectionManager connectionManager) {

        // 1. 构建同步HttpClient
        HttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        // 2. 绑定到RestClient
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(60000);

        return RestClient.builder(restTemplate)
                .requestFactory(requestFactory);
    }

    

}
