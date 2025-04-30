package com.hd.ai.rag.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hd.ai.rag.common.IdUtil;
import com.hd.ai.rag.entity.DemandDocument;
import com.hd.ai.rag.entity.DesignDocument;
import com.hd.ai.rag.mapper.DemandDocMapper;
import com.hd.ai.rag.service.DemandDocService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// DemandDocServiceImpl.java
@Service
public class DemandDocServiceImpl extends ServiceImpl<DemandDocMapper, DemandDocument>
        implements DemandDocService {

    @Transactional
    public boolean delDocument(String id) {
        return this.delDocument(id);
    }

    @Transactional
    public boolean saveDocument(DemandDocument doc) {
        if (StringUtils.isBlank(doc.getId())) {
            doc.setId(IdUtil.getSnowflakeNextIdStr());
        }
        return this.saveOrUpdate(doc);
    }

    @Override
    public List<DemandDocument> getDocumentTree(String projectId,String branchId, String tagId) {
        List<DemandDocument> list = lambdaQuery().eq(StrUtil.isNotBlank(projectId), DemandDocument::getProjectId, projectId)
                .eq(StrUtil.isNotBlank(branchId), DemandDocument::getBranchId, branchId)
                .eq(StrUtil.isNotBlank(tagId), DemandDocument::getTagId, tagId)
                .list();
        return buildTree(list);
    }

    private List<DemandDocument> buildTree(List<DemandDocument> nodes) {
        Map<String, DemandDocument> nodeMap = new HashMap<>();
        List<DemandDocument> roots = new ArrayList<>();

        nodes.forEach(node -> nodeMap.put(node.getId(), node));
        nodes.forEach(node -> {
            if ("0".equals(node.getPid())) {
                roots.add(node);
            } else {
                DemandDocument parent = nodeMap.get(node.getPid());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        });
        return roots;
    }
}
