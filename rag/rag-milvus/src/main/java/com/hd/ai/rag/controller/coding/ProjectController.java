package com.hd.ai.rag.controller.coding;

import com.hd.ai.rag.common.AjaxResult;
import com.hd.ai.rag.entity.Branch;
import com.hd.ai.rag.entity.Project;
import com.hd.ai.rag.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @PostMapping("/save")
    public AjaxResult<Boolean> save(@RequestBody Project project) {
        return AjaxResult.success(projectService.saveProject(project));
    }

    @PostMapping("/delete")
    public AjaxResult<Boolean> delete(String id) {
        return AjaxResult.success(projectService.del(id));
    }

    @PostMapping("/list")
    public AjaxResult<Project> list(String title, Integer currentPage, Integer pageSize) {
        return AjaxResult.success(projectService.list(title,currentPage,pageSize));
    }
}
