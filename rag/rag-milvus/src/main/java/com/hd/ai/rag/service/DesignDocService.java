package com.hd.ai.rag.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hd.ai.rag.common.IdUtil;
import com.hd.ai.rag.entity.DemandDocument;
import com.hd.ai.rag.entity.DesignDocument;
import com.hd.ai.rag.mapper.DesignDocMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// DemandDocServiceImpl.java
@Service
public class DesignDocService extends ServiceImpl<DesignDocMapper, DesignDocument>
        {

    @Transactional
    public boolean delDocument(String id) {
        return this.removeById(id);
    }

    @Transactional
    public boolean saveDocument(DesignDocument doc) {
        if (StringUtils.isBlank(doc.getId())) {
            doc.setId(IdUtil.getSnowflakeNextIdStr());
        }
        if(StrUtil.isBlank(doc.getPid()))
        {
            doc.setPid("0");
        }
        return this.saveOrUpdate(doc);
    }

    public List<DesignDocument> getDocumentTree(String pid,String projectId,String branchId, String tagId,String title,Integer currentPage,Integer pageSize) {
        Page<DesignDocument> page = new Page<>(currentPage, pageSize);
        List<DesignDocument> list = lambdaQuery()
                .eq(StrUtil.isNotBlank(pid), DesignDocument::getPid, pid)
                .eq(StrUtil.isNotBlank(projectId), DesignDocument::getProjectId, projectId)
                .eq(StrUtil.isNotBlank(branchId), DesignDocument::getBranchId, branchId)
                .eq(StrUtil.isNotBlank(tagId), DesignDocument::getTagId, tagId)
                .orderByDesc(DesignDocument::getCreateDate)
                .like(StrUtil.isNotBlank(title), DesignDocument::getTitle, title)
                .page(page).getRecords();
        return buildTree(list);
    }

    private List<DesignDocument> buildTree(List<DesignDocument> nodes) {
        Map<String, DesignDocument> nodeMap = new HashMap<>();
        List<DesignDocument> roots = new ArrayList<>();

        nodes.forEach(node -> nodeMap.put(node.getId(), node));
        nodes.forEach(node -> {
            if ("0".equals(node.getPid())) {
                roots.add(node);
            } else {
                DesignDocument parent = nodeMap.get(node.getPid());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        });
        return roots;
    }
}
