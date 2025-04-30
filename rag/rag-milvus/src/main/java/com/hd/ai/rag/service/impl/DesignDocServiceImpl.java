package com.hd.ai.rag.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hd.ai.rag.common.IdUtil;
import com.hd.ai.rag.entity.DemandDocument;
import com.hd.ai.rag.entity.DesignDocument;
import com.hd.ai.rag.mapper.DemandDocMapper;
import com.hd.ai.rag.mapper.DesignDocMapper;
import com.hd.ai.rag.service.DemandDocService;
import com.hd.ai.rag.service.DesignDocService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// DemandDocServiceImpl.java
@Service
public class DesignDocServiceImpl extends ServiceImpl<DesignDocMapper, DesignDocument>
        implements DesignDocService {

    @Transactional
    @Override
    public boolean delDocument(String id) {
        return this.delDocument(id);
    }

    @Transactional
    @Override
    public boolean saveDocument(DesignDocument doc) {
        if (StringUtils.isBlank(doc.getId())) {
            doc.setId(IdUtil.getSnowflakeNextIdStr());
        }
        return this.saveOrUpdate(doc);
    }

    @Override
    public List<DesignDocument> getDocumentTree(String projectId,String branchId, String tagId) {
        List<DesignDocument> list = lambdaQuery().eq(StrUtil.isNotBlank(projectId), DesignDocument::getProjectId, projectId)
                .eq(StrUtil.isNotBlank(branchId), DesignDocument::getBranchId, branchId)
                .eq(StrUtil.isNotBlank(tagId), DesignDocument::getTagId, tagId)
                .list();
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
