package com.alibaba.cloud.ai.example.rag.util;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.index.request.CreateIndexReq;
import io.milvus.v2.service.vector.request.DeleteReq;
import io.milvus.v2.service.vector.request.GetReq;
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.DeleteResp;
import io.milvus.v2.service.vector.response.GetResp;
import io.milvus.v2.service.vector.response.InsertResp;
import io.milvus.v2.service.vector.response.SearchResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 向量数据库工具类
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class MilvusUtil {
    @Autowired
    private DashScopeEmbeddingModel embeddingModel;
    @Autowired
    private final MilvusClientV2 milvusClient;

    /**
     * - 创建集合
     */
    public void createCollection(String tableName) {
        // 创建字段
        CreateCollectionReq.CollectionSchema schema = milvusClient.createSchema();
        // 创建主键字段
        schema.addField(AddFieldReq.builder()
                .fieldName("doc_id")
                .description("主键ID")
                .dataType(DataType.Int64)
                // 是否为主键
                .isPrimaryKey(true)
                // 设置主键自增
                .autoID(true)
                .build());
        schema.addField(AddFieldReq.builder()
                .fieldName("source")
                .description("文件名")
                .dataType(DataType.VarChar)
                // 设置字段为可空
                .build());
        // 创建特征向量字段
        schema.addField(AddFieldReq.builder()
                // 字段名
                .fieldName("embedding")
                // 字段描述
                .description("特征向量")
                // 字段类型
                .dataType(DataType.FloatVector)
                // 设置向量维度
                .dimension(1024)
                .build());
        schema.addField(AddFieldReq.builder()
                .fieldName("content")
                .description("文本內容")
                .dataType(DataType.VarChar)
                .build());
        schema.addField(AddFieldReq.builder()
                .fieldName("metadata")
                .description("元数据")
                .dataType(DataType.VarChar)
                .build());
        // 创建集合
        CreateCollectionReq collectionReq = CreateCollectionReq.builder()
                // 集合名称
                .collectionName(tableName)
                // 集合描述
                .description("自定义知识库")
                // 集合字段
                .collectionSchema(schema)
                // 分片数量
                .numShards(10)
                .build();
        milvusClient.createCollection(collectionReq);
        // 创建索引
        IndexParam indexParam = IndexParam.builder()
                // 索引字段名
                .fieldName("embedding")
                // 索引类型
                .indexType(IndexParam.IndexType.IVF_FLAT)
                // 索引距离度量
                .metricType(IndexParam.MetricType.COSINE)
                .build();
        CreateIndexReq createIndexReq = CreateIndexReq.builder()
                .collectionName(tableName)
                .indexParams(Collections.singletonList(indexParam))
                .build();
        milvusClient.createIndex(createIndexReq);
    }

    /**
     * 批量插入数据
     *
     * @param vectorParam 向量参数
     * @param text        文本
     * @param metadata    元数据
     * @param source      来源
     */
    public int batchInsert(List<String> vectorParam, List<String> text, Map<String, Object> metadata, List<String> source) {
        AtomicInteger index = new AtomicInteger(0);
        if (vectorParam != null && vectorParam.size() > 0) {
            text.stream().peek(content ->
                    {
                        try {
                            Document textDocument = new Document(content, metadata);
                            //修改内容
                            textDocument.getMetadata().put("source", source.get(index.get()));
                            textDocument.getMetadata().put("order", index.get());
                        } catch (Exception e) {
                        }
                        index.getAndIncrement();
                    }
            );
        }
        return index.get();
    }


    public InsertResp insert(String docId, float[] vectorParam, String text, String metadata, String fileName, String tableName) {
// 校验集合是否存在
        JsonObject jsonObject = new JsonObject();
// 数组转换成JsonElement
        jsonObject.add("doc_id", new Gson().toJsonTree(docId));
        jsonObject.add("embedding", new Gson().toJsonTree(vectorParam));
        jsonObject.add("content", new Gson().toJsonTree(text));
        jsonObject.add("metadata", new Gson().toJsonTree(metadata));
        jsonObject.add("source", new Gson().toJsonTree(fileName));

        InsertReq insertReq = InsertReq.builder()
// 集合名称
                .collectionName(tableName)
                .data(Collections.singletonList(jsonObject))
                .build();
        return milvusClient.insert(insertReq);
    }

    /**
     * 查询某个表的数据
     *
     * @param vectorParam 向量
     * @param tableName   表
     * @param top         返回数量
     * @return 查询应答
     */
    public List<List<SearchResp.SearchResult>> search(float[] vectorParam, String tableName, int top) {
        FloatVec floatVec = new FloatVec(vectorParam);
        SearchReq.SearchReqBuilder sb = SearchReq.builder();
        if (top > 0) {
            sb.topK(top);
        }
        SearchReq searchReq = sb
                // 集合名称
                .collectionName(tableName)
                // 搜索向量
                .data(Collections.singletonList(floatVec))
                // 搜索字段
                .annsField("embedding")
                // 返回字段
//        .outputFields(Arrays.asList("doc_id", "content", "metadata", "source"))
                .outputFields(Arrays.asList("*"))
                .build();
        SearchResp searchR = milvusClient.search(searchReq);
        List<List<SearchResp.SearchResult>> searchResults = searchR.getSearchResults();
        for (List<SearchResp.SearchResult> results : searchResults) {
            for (SearchResp.SearchResult result : results) {
                log.info("ID=" + (String) result.getId() + ",Score=" + result.getScore() + ",Result=" + result.getEntity().toString());
            }
        }
        return searchResults;
    }


    /**
     * 通过ID获取记录
     */
    public GetResp getRecord(String id, String tableName) {
        GetReq getReq = GetReq.builder()
                .collectionName(tableName)
                .ids(Collections.singletonList(id))
                .build();
        GetResp resp = milvusClient.get(getReq);
        return resp;
    }

    /**
     * 通过ID删除记录
     */
    public DeleteResp delRecord(String id, String tableName) {
        DeleteReq deleteReq = DeleteReq.builder()
                .collectionName(tableName)
                .ids(Collections.singletonList(id))
                .build();
        DeleteResp resp = milvusClient.delete(deleteReq);
        return resp;
    }

}
