package com.hd.ai.rag.controller.coding;

import com.aliyuncs.utils.StringUtils;
import com.hd.ai.rag.common.AjaxResult;
import com.hd.ai.rag.common.IdUtil;
import com.hd.ai.rag.common.PageData;
import com.hd.ai.rag.dto.DesignDocumentDto;
import com.hd.ai.rag.entity.Branch;
import com.hd.ai.rag.entity.DesignDocument;
import com.hd.ai.rag.entity.Project;
import com.hd.ai.rag.entity.Tag;
import com.hd.ai.rag.service.BranchService;
import com.hd.ai.rag.service.DesignDocService;
import com.hd.ai.rag.service.ProjectService;
import com.hd.ai.rag.service.TagService;
import com.hd.ai.rag.util.MarkdownProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/design-docs")
public class DesignDocController {
    @Autowired
    private DesignDocService docService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TagService tagService;
    @Autowired
    private BranchService branchService;

    @GetMapping("/index")
    public ModelAndView indexPage() {
        ModelAndView modelAndView = new ModelAndView("design_doc"); // 跳转到page2.html
        return modelAndView;
    }

    @GetMapping(path = "/loadBasicInfo")
    public AjaxResult loadBasicInfo() { // 接收文件) {
        Map<String, Object> data = new HashMap<>();
        List<Project> projects=projectService.listAll();
        List<Tag> tags=tagService.listAll();
        List<Branch> branchs=branchService.listAll();
        data.put("projects", projects);
        data.put("tags", tags);
        data.put("branchs", branchs);
        return AjaxResult.success(data);
    }

    @PostMapping(path = "/saveOrUpdate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult<DesignDocument> saveOrUpdate(@ModelAttribute DesignDocumentDto designDocumentDto, // 接收普通字段
                                           @RequestPart(name="content",required=false) MultipartFile file) { // 接收文件) {
        DesignDocument doc=new DesignDocument();
        if(StringUtils.isEmpty(designDocumentDto.getId()))
        {
            doc.setId(IdUtil.getSnowflakeNextIdStr());
            doc.setPid(designDocumentDto.getPid());
        }else
        {//加载原始数据
            doc=docService.getById(designDocumentDto.getId());
        }
        doc.setTitle(designDocumentDto.getText());
        doc.setTagId(designDocumentDto.getTagId());
        doc.setBranchId(designDocumentDto.getBranchId());
        doc.setProjectId(designDocumentDto.getProjectId());
        doc.setType(designDocumentDto.getType());
        try {//更新内容
            if(file!=null)
            {
                String docContent=MarkdownProcessor.process(file.getInputStream());
                doc.setContent(docContent);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        boolean isSuccess=docService.saveOrUpdate(doc);
        return AjaxResult.success();
    }

    @PostMapping("get")
    public AjaxResult<DesignDocument> get(String id) {
        DesignDocument document=docService.getById(id);
        Map<String, Object> data = new HashMap<>();
        List<Project> projects=projectService.listAll();
        List<Tag> tags=tagService.listAll();
        List<Branch> branchs=branchService.listAll();
        data.put("projects", projects);
        data.put("tags", tags);
        data.put("branchs", branchs);
        data.put("document", document);
        return AjaxResult.success(data);
    }

    @PostMapping("delete")
    public AjaxResult deleteDoc(String id) {
        boolean result=docService.delDocument(id);
        return AjaxResult.success();
    }
    @PostMapping("getContent")
    public AjaxResult getContent(String id) {
        Map<String, String> data = new HashMap<>();
        DesignDocument document=docService.getById(id);
        String content=document.getContent();
        data.put("content", content);
        return AjaxResult.success(data);
    }

    @PostMapping("/list")
    public PageData<DesignDocumentDto> list(String projectId, String branchId, String tagId, String title,Integer type,String beginDate,String endDate,Integer pageNo, Integer pageSize) {
        PageData<DesignDocumentDto> page=docService.getDocumentTree(projectId,branchId,tagId,title,type,beginDate,endDate,pageNo,pageSize);
        return page;
    }
}
