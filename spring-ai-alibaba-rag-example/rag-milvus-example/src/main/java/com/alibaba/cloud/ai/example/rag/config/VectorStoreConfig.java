package com.alibaba.cloud.ai.example.rag.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class VectorStoreConfig {
    @Value("${spring.ai.vectorstore.milvus.client.host}")
    private String host;
    @Value("${spring.ai.vectorstore.milvus.client.port}")
    private Integer port;
    @Value("${spring.ai.vectorstore.milvus.client.username}")
    private String username;
    @Value("${spring.ai.vectorstore.milvus.client.password}")
    private String password;

    @Value("${spring.ai.dashscope.embedding.options.model}")
    private String embeddingModel;
    @Value("${spring.ai.dashscope.embedding.options.dimensions}")
    private Integer embeddingDimensions;
    /**
     * 定义一个名为 milvusServiceClient 的Bean，用于创建并返回一个 MilvusServiceClient 实例。
     */
    @Bean
    public MilvusServiceClient milvusServiceClient() {
        return new MilvusServiceClient(
                ConnectParam.newBuilder()
                        .withHost(host)
                        .withPort(port)
                        .withAuthorization(username,password)
                        .build());
    }
    @Bean("dashScopeEmbeddingModel")
    public DashScopeEmbeddingModel dashScopeEmbeddingModel()
    {
        DashScopeApi dashScopeApi = new DashScopeApi(System.getenv("DASHSCOPE_API_KEY"));
        DashScopeEmbeddingModel dashScopeEmbeddingModel = new DashScopeEmbeddingModel(dashScopeApi, MetadataMode.EMBED,
                DashScopeEmbeddingOptions.builder()
                        .withModel(embeddingModel).withDimensions(embeddingDimensions)
                        .build());
        return dashScopeEmbeddingModel;
    }
    /**
     * 定义一个名为 vectorStore2 的Bean，用于创建并返回一个 VectorStore 实例。
     * 使用 MilvusVectorStore.builder 方法构建向量存储对象，并设置以下参数：
     * collectionName：集合名称为 "vector_store_02"。
     * databaseName：数据库名称为 "default"。
     * embeddingDimension：嵌入维度为 1536。
     * indexType：索引类型为 IVF_FLAT，这是一种常见的近似最近邻搜索索引类型。
     * metricType：度量类型为 COSINE，用于计算向量之间的余弦相似度。
     * batchingStrategy：使用 TokenCountBatchingStrategy 策略进行批量处理。
     * initializeSchema：设置为 true，表示在构建时初始化数据库模式。
     */
    @Bean(name = "vectorStore2")
    public MilvusVectorStore vectorStore(MilvusServiceClient milvusClient, @Qualifier("dashScopeEmbeddingModel") EmbeddingModel embeddingModel) {
        return MilvusVectorStore.builder(milvusClient, embeddingModel)
                .collectionName("vector_store_02")
                .databaseName("test")
                .embeddingDimension(1024)
                .indexType(IndexType.IVF_FLAT)
                .metricType(MetricType.COSINE)
                .batchingStrategy(new TokenCountBatchingStrategy())
                .initializeSchema(true)
                .build();
    }
}
