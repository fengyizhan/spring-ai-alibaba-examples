package com.hd.ai.rag.controller.coding;

import com.hd.ai.rag.common.AjaxResult;
import com.hd.ai.rag.entity.Project;
import com.hd.ai.rag.service.impl.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @PostMapping
    public AjaxResult<Project> create(@RequestBody Project project) {
        projectService.save(project);
        return AjaxResult.success(project);
    }

    @GetMapping("/{id}")
    public AjaxResult<Project> getById(@PathVariable String id) {
        return AjaxResult.success(projectService.getById(id));
    }

    @PutMapping
    public AjaxResult<Boolean> update(@RequestBody Project project) {
        return AjaxResult.success(projectService.updateById(project));
    }

    @DeleteMapping("/{id}")
    public AjaxResult<Boolean> delete(@PathVariable String id) {
        return AjaxResult.success(projectService.removeById(id));
    }
}
