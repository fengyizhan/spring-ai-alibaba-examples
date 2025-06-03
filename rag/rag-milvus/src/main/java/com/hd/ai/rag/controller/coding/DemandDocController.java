package com.hd.ai.rag.controller.coding;

import com.aliyuncs.utils.StringUtils;
import com.hd.ai.rag.common.AjaxResult;
import com.hd.ai.rag.common.IdUtil;
import com.hd.ai.rag.common.PageData;
import com.hd.ai.rag.dto.DemandDocumentDto;
import com.hd.ai.rag.entity.Branch;
import com.hd.ai.rag.entity.DemandDocument;
import com.hd.ai.rag.entity.Project;
import com.hd.ai.rag.entity.Tag;
import com.hd.ai.rag.service.BranchService;
import com.hd.ai.rag.service.DemandDocService;
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
@RequestMapping("/api/demand-docs")
public class DemandDocController {
    @Autowired
    private DemandDocService docService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TagService tagService;
    @Autowired
    private BranchService branchService;

    @GetMapping("/index")
    public ModelAndView indexPage() {
        ModelAndView modelAndView = new ModelAndView("demand_doc"); // 跳转到page2.html
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
    public AjaxResult<DemandDocument> saveOrUpdate(@ModelAttribute DemandDocumentDto demandDocumentDto, // 接收普通字段
                                                   @RequestPart(name="content",required=false) MultipartFile file) { // 接收文件) {
        DemandDocument doc=new DemandDocument();
        if(StringUtils.isEmpty(demandDocumentDto.getId()))
        {
            doc.setId(IdUtil.getSnowflakeNextIdStr());
            doc.setPid(demandDocumentDto.getPid());
        }else
        {//加载原始数据
            doc=docService.getById(demandDocumentDto.getId());
        }
        doc.setTitle(demandDocumentDto.getText());
        doc.setTagId(demandDocumentDto.getTagId());
        doc.setBranchId(demandDocumentDto.getBranchId());
        doc.setProjectId(demandDocumentDto.getProjectId());
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
    public AjaxResult<DemandDocument> get(String id) {
        DemandDocument document=docService.getById(id);
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
        DemandDocument document=docService.getById(id);
        String content=document.getContent();
        data.put("content", content);
        return AjaxResult.success(data);
    }

    @PostMapping("/list")
    public PageData<DemandDocumentDto> list(String projectId, String branchId, String tagId, String title,String beginDate,String endDate,Integer pageNo, Integer pageSize) {
        PageData<DemandDocumentDto> page=docService.getDocumentTree(projectId,branchId,tagId,title,beginDate,endDate,pageNo,pageSize);
        return page;
    }
}
