package com.hd.ai.rag.controller.coding;

import com.hd.ai.rag.common.AjaxResult;
import com.hd.ai.rag.common.IdUtil;
import com.hd.ai.rag.entity.DemandDocument;
import com.hd.ai.rag.service.DemandDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;

@RestController
@RequestMapping("/api/demand-doc")
public class DemandDocController {
    @Autowired
    private DemandDocService docService;

    @PostMapping
    public AjaxResult<DemandDocument> save(@RequestBody DemandDocument doc) {
        doc.setId(IdUtil.getSnowflakeNextIdStr());
        doc.setCreateDate(LocalDateTime.now());
        return AjaxResult.success(docService.save(doc));
    }

    @GetMapping("/tree")
    public AjaxResult<DemandDocument> getTree(@RequestParam String branchId,
                          @RequestParam String tagId) {
        return AjaxResult.success(docService.getDocumentTree(branchId, tagId));
    }
}
