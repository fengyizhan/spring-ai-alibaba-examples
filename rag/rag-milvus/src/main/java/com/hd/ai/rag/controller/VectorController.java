package com.hd.ai.rag.controller;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.hd.ai.rag.util.TokenTextSplitterExtends;
import com.hd.ai.rag.util.TikaUtil;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/vector")
@CrossOrigin(origins = "*")
public class VectorController {

    @Autowired
    private DashScopeEmbeddingModel embeddingModel;
    @Autowired
    @Qualifier("vectorStore2")
    private MilvusVectorStore vectorStore;
    @Autowired
    private ChatClient chatClient;
    private ChatMemory chatMemory;
    @Autowired
    private ChatModel chatModel;
    @Autowired
    private ChatClient.Builder builder;

    @Autowired
    private TikaUtil tikaUtil;

    @Value("classpath:/template/table.txt")
    private Resource tableResource;

    @Value("classpath:/template/tableQuery.txt")
    private Resource tableQueryResource;

    @Value("classpath:/template/rag.txt")
    private Resource ragResource;
    @PostConstruct
    public void init()
    {
        chatMemory=new InMemoryChatMemory();
        this.chatClient = this.builder.defaultAdvisors(new MessageChatMemoryAdvisor(this.chatMemory))
                .build();
    }

    /**
     * input array<string> 或 string 或 file 必选
     *
     * 输入文本的基本信息，可以是字符串，字符串列表， 或者打开的文件（需要Embedding的内容，一行一条）。
     * 文本限制：
     *
     * 作为字符串时最长支持 2,048 Token。
     *
     * text-embedding-v3模型input是字符串时，最长支持 8,192 Token。约12288汉字=32768个字母
     *
     * text-embedding-v3模型input是字符串列表时最多支持 10 条，每条最长支持 8,192 Token。
     *
     * text-embedding-v3模型input是文本文件时最多支持 10 行，每行最长支持 8,192 Token。
     *
     * Token 与字符的转换关系‌
     * 1. 汉字与Token的对应‌
     *
     * ‌通用规则‌：
     * 1个Token通常对应 1个汉字‌（如通义千问、千帆等模型）‌37。
     * ‌部分模型差异‌：某些模型可能采用子词切分，1个Token对应 1.5–1.8个汉字‌（如腾讯模型）‌35。
     * 2. 英文字母与Token的对应‌
     *
     * ‌常规比例‌：1个Token平均覆盖 3–4个英文字母‌（如“apple”计为1个Token）‌37。
     * ‌细分规则‌：英文分词可能按单词或子词划分（如“unhappiness”拆分为“un”+“happiness”）‌8。
     * 3. 转换比率总结‌
     *
     * 语言	字符类型	每Token对应字符数	每字符对应Token数
     * 中文	汉字	1–1.8个‌	‌≈0.5–1个‌
     * 英文	字母	3–4个‌	‌≈0.3个
     *
     *
     * @param input 需要解析的文本，有长度限制，需要自己做截断
     * @return
     * @throws IOException
     */
    @GetMapping("/embed")
    public List<Float> embed(String input) throws IOException {
        float[] result=embeddingModel.embed(input);
        List<Float> embeddings=new ArrayList<>();
        for(int i=0;i<result.length;i++)
        {
            embeddings.add(result[i]);
        }
        log.info(embeddings);
        return embeddings;
    }
    /**
     * 处理PDF文档的解析、分割和嵌入存储。
     * 使用 PagePdfDocumentReader 解析PDF文档并生成 Document 列表。
     * 使用 TokenTextSplitter 将文档分割成更小的部分。
     * 将分割后的文档添加到向量存储中，以便后续检索和生成。
     */
    @GetMapping("/insertDocuments")
    public void insertDocuments() throws IOException {
        // 1. parse document
        try
        {
            TikaDocumentReader reader = new TikaDocumentReader(tableResource);
            List<Document> documents = reader.get();
            List<Document> newDocuments = new ArrayList<>();
            log.info("{} documents loaded", documents.size());
            // 2. split trunks
            List<Document> splitDocuments = new TokenTextSplitterExtends("######").apply(documents);
            splitDocuments.parallelStream().forEach( document ->
            {
                String id = UUID.randomUUID().toString();
                newDocuments.add(new Document(id,document.getText(),document.getMetadata()));
            } );
            log.info("{} documents split", newDocuments.size());
            // 3. create embedding and store to vector store
            log.info("create embedding and save to vector store");
            vectorStore.add(splitDocuments);
        }catch (Exception e)
        {
            log.info(e.getMessage());
        }
    }

    /**
     * 处理文档的解析、分割和嵌入存储。
     * 使用 PagePdfDocumentReader 解析PDF文档并生成 Document 列表。
     * 使用 TokenTextSplitter 将文档分割成更小的部分。
     * 将分割后的文档添加到向量存储中，以便后续检索和生成。
     */
    @GetMapping("/splitDocuments")
    public void splitDocuments() throws IOException {
        // 1. parse document
        try
        {
            TikaDocumentReader reader = new TikaDocumentReader(tableResource);
            List<Document> documents = reader.get();
            List<Document> splitDocuments = new ArrayList<>();
            List<Document> newDocuments = new ArrayList<>();
            log.info("{} documents loaded", documents.size());
            // 2. 文本切分为段落
            TextSplitter splitter = new TokenTextSplitter(1200, 350, 5, 100, true);
            splitDocuments = splitter.apply(documents);
            splitDocuments.parallelStream().forEach( document ->
            {
                String id = UUID.randomUUID().toString();
                newDocuments.add(new Document(id,document.getText(),document.getMetadata()));
            } );
            log.info("{} documents split", newDocuments.size());
        }catch (Exception e)
        {
            log.info(e.getMessage());
        }
    }



    @PostMapping("/fileSplit")
    public ResponseEntity<List<String>> fileSplit(MultipartFile file) {
        return ResponseEntity.ok(tikaUtil.splitParagraphsHanLP(tikaUtil.extractTextString(file)));
    }



    @GetMapping(value = "/tableSql", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> tableSql(String message) throws IOException {
        SearchRequest searchRequest = SearchRequest.builder().topK(10).similarityThreshold(0.5).build();

        String promptTemplate = tableQueryResource.getContentAsString(StandardCharsets.UTF_8);
        SystemPromptTemplate systemPromptTemplate=new SystemPromptTemplate(promptTemplate);
        String tableSql = tableResource.getContentAsString(StandardCharsets.UTF_8);
        String finalContent=systemPromptTemplate.create(Map.of("memory",tableSql)).getContents();
        return chatClient
                .prompt()
                // 输入多条消息，消息可以分角色
                .messages(new SystemMessage(finalContent),
                        new UserMessage(message))
                .stream()
                // 构造SSE（ServerSendEvent）格式返回结果
                .chatResponse()
                .map(chatResponse -> {
                    var result=chatResponse.getResult().getOutput().getText();
                    //{"$ref":"$.media"}
                    // 手动转义正则中的特殊字符
                    log.info("str=="+result);
                    return ServerSentEvent.builder(
                                    result
                            )
                            .event("message").build();
                });
    }

    @GetMapping("/add")
    public boolean addKnowledage(@RequestParam(value = "meta-message") String message, @RequestParam(value = "vector-content") String content) {
        String uuid = UUID.randomUUID().toString();
//        DocumentInfoPO documentInfoPO = new DocumentInfoPO();
//        documentInfoPO.setVectorId(uuid);
//        documentInfoPO.setMetaMessage(message);
//        documentInfoPO.setVectorContent(content);
//        documentInfoPOMapper.insert(documentInfoPO);
        List<Document> documents = List.of(
                new Document(uuid, content, Map.of("text", message)));
        vectorStore.add(documents);
        return true;
    }

    @GetMapping("/search")
    public List<Document> selectKnowledage(@RequestParam(value = "vector-content") String content) {
        SearchRequest searchRequest = SearchRequest.builder().query(content).topK(20).similarityThreshold(0.5).build();
        List<Document> result = vectorStore.similaritySearch(searchRequest);
        return result;
    }

    @GetMapping("/delete")
    public Boolean deleteKnowledage(@RequestParam(value = "vector-id") String[] ids) {
        try {
            vectorStore.delete(List.of(ids));
        } catch (Exception e) {
            return false;
        } finally {
            return true;
        }

    }
}