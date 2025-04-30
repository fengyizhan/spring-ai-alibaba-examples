package com.hd.ai.rag.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hd.ai.rag.entity.DesignDocument;

import java.util.List;

// DemandDocService.java
public interface DesignDocService extends IService<DesignDocument> {
    public boolean delDocument(String id);
    public boolean saveDocument(DesignDocument doc);
    public List<DesignDocument> getDocumentTree(String projectId,String branchId, String tagId);
}
