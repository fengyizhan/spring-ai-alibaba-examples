package com.hd.ai.rag.dto;

import lombok.Data;

// DemandDocumentDto.java
@Data
public class DemandDocumentDto {

    private String id;
    private String pid;
    private String text;

    private String branchId;
    private String tagId;
    private String projectId;

    public DemandDocumentDto(String id, String pid, String title) {
        this.id = id;
        this.pid = pid;
        this.text = title;
    }
}

