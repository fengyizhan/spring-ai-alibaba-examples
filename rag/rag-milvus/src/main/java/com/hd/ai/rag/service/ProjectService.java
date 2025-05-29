package com.hd.ai.rag.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hd.ai.rag.common.IdUtil;
import com.hd.ai.rag.entity.Project;
import com.hd.ai.rag.mapper.ProjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService extends ServiceImpl<ProjectMapper, Project> {
    @Transactional
    public boolean del(String id) {
        return this.removeById(id);
    }

    @Transactional
    public boolean saveProject(Project project) {
        if (StringUtils.isBlank(project.getId())) {
            project.setId(IdUtil.getSnowflakeNextIdStr());
        }
        return this.saveOrUpdate(project);
    }

    public List<Project> list(String title, Integer currentPage, Integer pageSize) {
        Page<Project> page = new Page<>(currentPage, pageSize);
        List<Project> list = lambdaQuery()
                .and(StrUtil.isNotBlank(title), con->
                        {
                            con.like(Project::getTitle, title)
                                    .or()
                                    .like(Project::getNote, title);
                        }
                )
                .orderByDesc(Project::getCreateDate)
                .page(page).getRecords();
        return list;
    }
    public List<Project> listAll() {
        List<Project> list = this.list();
        return list;
    }
}
