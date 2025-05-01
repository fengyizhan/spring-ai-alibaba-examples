package com.hd.ai.rag.controller.coding;

import com.hd.ai.rag.common.AjaxResult;
import com.hd.ai.rag.common.IdUtil;
import com.hd.ai.rag.entity.DesignDocument;
import com.hd.ai.rag.service.DesignDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/design-doc")
public class DesignDocController {
    @Autowired
    private DesignDocService docService;

    @PostMapping("/save")
    public AjaxResult<DesignDocument> save(@RequestBody DesignDocument doc) {
        doc.setId(IdUtil.getSnowflakeNextIdStr());
        doc.setCreateDate(LocalDateTime.now());
        return AjaxResult.success(docService.save(doc));
    }
    @PostMapping("delete")
    public AjaxResult<DesignDocument> del(String id) {
        return AjaxResult.success(docService.delDocument(id));
    }


    @PostMapping("/tree")
    public AjaxResult<DesignDocument> getTree(String pid,String projectId,String branchId, String tagId,String title,Integer currentPage,Integer pageSize) {
        return AjaxResult.success(docService.getDocumentTree(pid,projectId,branchId,tagId,title,currentPage,pageSize));
    }
}
