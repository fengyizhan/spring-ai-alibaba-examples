package com.hd.ai.rag.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hd.ai.rag.entity.DemandDocument;

import java.util.List;

// DemandDocService.java
public interface DemandDocService extends IService<DemandDocument> {
    public boolean delDocument(String id);
    public boolean saveDocument(DemandDocument doc);
    public List<DemandDocument> getDocumentTree(String projectId,String branchId, String tagId);
}
