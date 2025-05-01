package com.hd.ai.rag.controller.coding;

import com.hd.ai.rag.common.AjaxResult;
import com.hd.ai.rag.entity.Project;
import com.hd.ai.rag.entity.Tag;
import com.hd.ai.rag.service.ProjectService;
import com.hd.ai.rag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tag")
public class TagController {
    @Autowired
    private TagService tagService;

    @PostMapping("/save")
    public AjaxResult<Boolean> save(@RequestBody Tag tag) {
        return AjaxResult.success(tagService.saveTag(tag));
    }

    @PostMapping("/delete")
    public AjaxResult<Boolean> delete(String id) {
        return AjaxResult.success(tagService.del(id));
    }
}
