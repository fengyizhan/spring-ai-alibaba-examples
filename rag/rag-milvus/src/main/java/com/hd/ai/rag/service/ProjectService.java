package com.hd.ai.rag.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hd.ai.rag.common.IdUtil;
import com.hd.ai.rag.entity.Project;
import com.hd.ai.rag.mapper.ProjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
