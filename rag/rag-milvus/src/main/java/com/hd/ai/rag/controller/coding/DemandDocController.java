package com.hd.ai.rag.controller.coding;

import com.hd.ai.rag.common.AjaxResult;
import com.hd.ai.rag.common.IdUtil;
import com.hd.ai.rag.entity.DemandDocument;
import com.hd.ai.rag.service.DemandDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/demand-doc")
public class DemandDocController {
    @Autowired
    private DemandDocService docService;

    @PostMapping("/save")
    public AjaxResult<DemandDocument> save(@RequestBody DemandDocument doc) {
        doc.setId(IdUtil.getSnowflakeNextIdStr());
        doc.setCreateDate(LocalDateTime.now());
        return AjaxResult.success(docService.save(doc));
    }
    @PostMapping("delete")
    public AjaxResult<DemandDocument> del(String id) {
        return AjaxResult.success(docService.delDocument(id));
    }


    @PostMapping("/tree")
    public AjaxResult<DemandDocument> getTree(String pid,String projectId,String branchId, String tagId,String title,Integer currentPage,Integer pageSize) {
        return AjaxResult.success(docService.getDocumentTree(pid,projectId,branchId,tagId,title,currentPage,pageSize));
    }
}
