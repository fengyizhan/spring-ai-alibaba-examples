//package com.hd.ai.rag.config;
//
//import org.apache.http.HeaderElement;
//import org.apache.http.HeaderElementIterator;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.conn.ConnectionKeepAliveStrategy;
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.conn.ssl.TrustAllStrategy;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.impl.client.IdleConnectionEvictor;
//import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
//import org.apache.http.message.BasicHeaderElementIterator;
//import org.apache.http.protocol.HTTP;
//import org.apache.http.protocol.HttpContext;
//import org.apache.http.ssl.SSLContexts;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.net.ssl.SSLContext;
//import java.security.KeyStore;
//import java.util.concurrent.TimeUnit;
//
//@Configuration
//public class HttpClientConfig {
//
//
//    @Value("${http_max_total:800}")
//    private int maxTotal = 800;
//
//    @Value("${http_default_max_perRoute:80}")
//    private int defaultMaxPerRoute = 80;
//
//    @Value("${http_validate_after_inactivity:5000}")
//    private int validateAfterInactivity = 5000;
//
//    @Value("${http_connection_request_timeout:50000}")
//    private int connectionRequestTimeout = 50000;
//
//    @Value("${http_connection_timeout:5000}")
//    private int connectTimeout = 5000;
//
//    @Value("${http_socket_timeout:300000}")
//    private int socketTimeout = 300000;
//
//    @Value("${waitTime:30000}")
//    private int waitTime = 30000;
//
//    @Value("${idleConTime:3}")
//    private int idleConTime = 3;
//
//    @Value("${retryCount:3}")
//    private int retryCount = 3;
//
//    @Bean
//    public PoolingHttpClientConnectionManager createPoolingHttpClientConnectionManager() {
//
//        PoolingHttpClientConnectionManager poolmanager = new PoolingHttpClientConnectionManager();
//        poolmanager.setMaxTotal(maxTotal);
//        poolmanager.setDefaultMaxPerRoute(defaultMaxPerRoute);
//        poolmanager.setValidateAfterInactivity(validateAfterInactivity);
//        return poolmanager;
//    }
//
//    @Bean
//    public CloseableHttpClient createHttpClient(PoolingHttpClientConnectionManager poolManager) {
//
//        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().setConnectionManager(poolManager);
//        httpClientBuilder.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
//
//
//            @Override
//            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
//
//                HeaderElementIterator iterator = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
//                while (iterator.hasNext()) {
//
//                    HeaderElement headerElement = iterator.nextElement();
//                    String param = headerElement.getName();
//                    String value = headerElement.getValue();
//                    if (null != value && param.equalsIgnoreCase("timeout")) {
//
//                        return Long.parseLong(value) * 1000;
//                    }
//                }
//                return 300 * 1000;
//            }
//        });
//        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(retryCount, false));
//        return httpClientBuilder.build();
//    }
//
//    @Bean
//    public SSLContext createSSLContext() throws Exception {
//
//        return SSLContexts.custom().loadTrustMaterial(KeyStore.getInstance(KeyStore.getDefaultType()), new TrustAllStrategy()).build();
//    }
//
//    @Bean
//    public SSLConnectionSocketFactory createSSLConnectionSocketFactory(SSLContext sslContext) {
//
//        return new SSLConnectionSocketFactory(sslContext,
//                new String[] {
//                        "TLSv1" },
//                null,
//                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
//    }
//
//    @Bean
//    public RequestConfig createRequestConfig() {
//
//        return RequestConfig.custom()
//                .setConnectionRequestTimeout(connectionRequestTimeout) 	// 从连接池中取连接的超时时间
//                .setConnectTimeout(connectTimeout)						// 连接超时时间
//                .setSocketTimeout(socketTimeout)						// 请求超时时间
//                .build();
//    }
//
//    @Bean
//    public IdleConnectionEvictor createIdleConnectionEvictor(PoolingHttpClientConnectionManager poolManager) {
//
//        IdleConnectionEvictor idleConnectionEvictor = new IdleConnectionEvictor(poolManager, idleConTime, TimeUnit.MINUTES);
//        return idleConnectionEvictor;
//    }
//}
