package com.hd.ai.rag.controller;

import com.alibaba.fastjson.JSON;
import com.hd.ai.rag.common.TreeNode;
import com.hd.ai.rag.config.ChatHistoryService;
import com.hd.ai.rag.entity.DemandDocument;
import com.hd.ai.rag.entity.DesignDocument;
import com.hd.ai.rag.service.DemandDocService;
import com.hd.ai.rag.service.DesignDocService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/coding/agent")
public class CodingAgentController {

    private final ChatClient chatClient;
    private final ChatClient chatClientCoder;
    private final ChatClient mcpClient;
    private final ChatMemory chatMemory;
    @Autowired
    private DesignDocService designDocService;
    @Autowired
    private DemandDocService demandDocService;

    @Value("${workspace.root}")
    private String workspaceDir;

    @Value("${workspace.chatHistory}")
    private Long chatHistorySize;

    @Value("classpath:/template/coding.txt")
    private Resource codingResource;

    @Value("classpath:/template/stepCoding.txt")
    private Resource stepCodingResource;

    @Autowired
    private ChatHistoryService chatHistoryService;

    public CodingAgentController(ChatClient.Builder builder, @Qualifier("mcpChatModel") ChatModel mcpChatModel,ChatMemory redisChatMemory) {
        this.chatMemory = redisChatMemory;
        MessageChatMemoryAdvisor mma = new MessageChatMemoryAdvisor(redisChatMemory);
        this.chatClient = builder.defaultAdvisors(mma).build();
        this.chatClientCoder=builder.defaultAdvisors(mma).build();
        this.mcpClient = ChatClient.builder(mcpChatModel).defaultAdvisors(mma).build();

    }

    @GetMapping("/coding")
    public ModelAndView codingPage() {
        ModelAndView modelAndView = new ModelAndView("coding"); // 跳转到page2.html
        return modelAndView;
    }

    @GetMapping("/fileTree")
    public ResponseEntity<List<TreeNode>> getDirectoryTree() {
        try {
            List<String> ignoredFiles=new ArrayList<>();
            ignoredFiles.add(".git");
            ignoredFiles.add("conf");
            ignoredFiles.add("deploy");
            ignoredFiles.add("doc");
            ignoredFiles.add(".editorconfig");
            ignoredFiles.add(".idea");
            ignoredFiles.add("establish");
            ignoredFiles.add(".gitignore");
            ignoredFiles.add("pom.xml");
            ignoredFiles.add(".github");
            ignoredFiles.add(".editorconfig");
            ignoredFiles.add("README.md");
            ignoredFiles.add("build");
            ignoredFiles.add("target");
            TreeNode rootNode=buildTree2(workspaceDir,ignoredFiles);
            return ResponseEntity.ok(rootNode.getChildren());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    public static TreeNode buildTree2(String rootPath, List<String> ignoredFiles) {
        File rootDir = new File(rootPath);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory path");
        }
        return buildNode(rootDir,ignoredFiles);
    }

    private static TreeNode buildNode(File file,List<String> ignoredFiles) {
        TreeNode node = new TreeNode();
        node.setId(file.getAbsolutePath());
        node.setTitle(file.getName());
        node.setType(file.isDirectory() ? "folder" : "file");

        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null && children.length == 1 && children[0].isDirectory()) {
                // 单子目录情况，递归合并
                TreeNode mergedNode = buildNode(children[0],ignoredFiles);
                node.setId(mergedNode.getId());
                node.setTitle(node.getTitle() + "." + mergedNode.getTitle());
                node.setChildren(mergedNode.getChildren());
            } else if (children != null && children.length > 0) {
                // 多子节点情况
                List<TreeNode> childNodes = new ArrayList<>();
                for (File child : children) {
                    //如果是文件，那么直接添加到【当前节点】的【子节点】列表中
                    if(ignoredFiles.indexOf(child.getName())>=0)
                    {//排除不需要展示的文件和目录
                        continue;
                    }
                    childNodes.add(buildNode(child,ignoredFiles));
                }
                node.setChildren(childNodes);
            }
        }
        return node;
    }


    private void buildTree(TreeNode rootNode,File currentFile, List<String> ignoredFiles) throws Exception {
        if (currentFile.isDirectory()) {
            File[] children = currentFile.listFiles();
            List<TreeNode> currentLevelChildNodes = new ArrayList<>();
            if (children != null) {
                for (File child : children) {
                    //如果是文件，那么直接添加到【当前节点】的【子节点】列表中
                    if(ignoredFiles.indexOf(child.getName())>=0)
                    {//排除不需要展示的文件和目录
                        continue;
                    }
                    TreeNode node = new TreeNode();
                    node.setId(child.getAbsolutePath());
                    node.setTitle(child.getName());
                    node.setType(child.isDirectory() ? "folder" : "file");
//                    node.setPath(child.getAbsolutePath());
                    if (child.isDirectory()) {
                        File currentFolder = child; //先假定目录的根是当前
                        while (currentFolder.listFiles()!=null&&currentFolder.listFiles().length==1)
                        {
                            currentFolder=currentFolder.listFiles()[0];
                        }
                        //如果是目录，递归向下继续遍历
                         buildTree(node,child,ignoredFiles);
                    }
                    currentLevelChildNodes.add(node);
                }
                rootNode.setChildren(currentLevelChildNodes);
            }
        }
    }

    /**
     * 开启新会话
     * @param userId 用户标识
     * @return 操作结果
     */
    @GetMapping("/clearSession")
    public ResponseEntity clearSession(@RequestParam String userId) {
        chatHistoryService.deleteConversation(userId);
        chatMemory.clear(userId); // 清空内存中的对话记录
        return ResponseEntity.ok().build();
    }


    @GetMapping(value = "/generate", produces = "text/event-stream;charset=UTF-8")
    public Flux<ServerSentEvent<String>> generate(@RequestParam String userId, @RequestParam String demandDocIds, @RequestParam String designDocIds,@RequestParam String codeSources) {

        String codingTemplate = null;
        String stepCodingTemplate = null;
        try {
            codingTemplate = codingResource.getContentAsString(StandardCharsets.UTF_8);
            stepCodingTemplate = stepCodingResource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SystemPromptTemplate systemPromptTemplate=new SystemPromptTemplate(codingTemplate);
        List<DemandDocument> demandDocuments=demandDocService.getBaseMapper().selectByIds(Arrays.asList(demandDocIds.split(",")));
        StringBuffer demandDocBuffer=new StringBuffer();
        //需求文档
        demandDocuments.forEach(demandDocument -> { demandDocBuffer.append(demandDocument.getContent()); });
        List<DesignDocument> designDocuments=designDocService.getBaseMapper().selectByIds(Arrays.asList(designDocIds.split(",")));
        StringBuffer designDocBuffer=new StringBuffer();
        StringBuffer codingRuleDocBuffer=new StringBuffer();
        StringBuffer projectDocBuffer=new StringBuffer();
        designDocuments.forEach(designDocument ->
        {//拆分出各种文档，
            int type=designDocument.getType().intValue();
            if (type==1)
            {//设计文档
                designDocBuffer.append(designDocument.getContent());
            }
            else if (type==2)
            {//编程规范
                codingRuleDocBuffer.append(designDocument.getContent());
            }
            else if (type==3)
            {//项目说明
                projectDocBuffer.append(designDocument.getContent());
            }
        });
        StringBuffer codingSourceBuffer=new StringBuffer();
        if(codeSources!=null)
        {
            String[] codeFilePath=codeSources.split(",");
            for(String codeFilePathStr:codeFilePath)
            {
                String codeStringContent= null;
                try {
                    codeStringContent = FileUtils.readFileToString(new File(codeFilePathStr), StandardCharsets.UTF_8)+"\n";
                    if(StringUtils.isNotBlank(codeStringContent))
                    {
                        codingSourceBuffer.append(codeStringContent);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        Map templateContentMap=new HashMap();

        templateContentMap.put("coding_rules",codingRuleDocBuffer.toString());
        templateContentMap.put("design_docs",designDocBuffer.toString());
        templateContentMap.put("project_docs",projectDocBuffer.toString());
        templateContentMap.put("demand_docs",demandDocBuffer.toString());
        templateContentMap.put("source_codes",codingSourceBuffer.toString());

        String finalContent=systemPromptTemplate.create(templateContentMap).getContents();
        return  chatClient
                .prompt()
                .messages(new UserMessage(finalContent))
                .advisors(spec -> spec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, userId)
                        // 继续在 Lambda 表达式中调用 .param 方法，设置聊天记忆的检索大小为 100
                        .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, chatHistorySize))
                .stream()
                // 构造SSE（ServerSendEvent）格式返回结果
                .chatResponse()
                .map(chatResponse -> {
                    var str= JSON.toJSONString(chatResponse.getResult().getOutput());
                    //{"$ref":"$.media"}
                    // 手动转义正则中的特殊字符
                    String result = str.replaceAll("\\{\\\"\\$ref\\\"\\:\\\"\\$\\.media\\\"\\}", "");
                    log.info("str=="+result);
                    return ServerSentEvent.builder(
                                    result
                            )
                            .event("message").build();
                });
    }




    @GetMapping(value = "/step-chat", produces = "text/event-stream;charset=UTF-8")
    public Flux<ServerSentEvent<String>> stepChat(@RequestParam String userId,@RequestParam String input) {
        return  chatClientCoder
                .prompt()
                .messages(
                        new UserMessage(input)
                )
                .advisors(spec -> spec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, userId)
                        // 继续在 Lambda 表达式中调用 .param 方法，设置聊天记忆的检索大小为 100
                        .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, chatHistorySize))
                .stream()
                // 构造SSE（ServerSendEvent）格式返回结果
                .chatResponse()
                .map(chatResponse -> {
                    var str= JSON.toJSONString(chatResponse.getResult().getOutput());
                    //{"$ref":"$.media"}
                    // 手动转义正则中的特殊字符
                    String result = str.replaceAll("\\{\\\"\\$ref\\\"\\:\\\"\\$\\.media\\\"\\}", "");
                    log.info("str=="+result);
                    return ServerSentEvent.builder(
                                    result
                            )
                            .event("message").build();
                });
    }



}