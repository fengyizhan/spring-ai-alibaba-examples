package com.hd.ai.rag.controller.coding;

import com.hd.ai.rag.common.AjaxResult;
import com.hd.ai.rag.entity.CodingRuleDocument;
import com.hd.ai.rag.entity.Tag;
import com.hd.ai.rag.service.CodingRuleDocService;
import com.hd.ai.rag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/codingrule")
public class CodingRuleDocController {
    @Autowired
    private CodingRuleDocService codingRuleDocService;

    @PostMapping("/save")
    public AjaxResult<Boolean> save(@RequestBody CodingRuleDocument document) {
        return AjaxResult.success(codingRuleDocService.saveRule(document));
    }

    @PostMapping("/delete")
    public AjaxResult<Boolean> delete(String id) {
        return AjaxResult.success(codingRuleDocService.del(id));
    }
}
