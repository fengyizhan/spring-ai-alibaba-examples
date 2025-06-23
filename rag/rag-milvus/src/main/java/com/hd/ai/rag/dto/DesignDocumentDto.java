package com.hd.ai.rag.dto;

import lombok.Data;

// DesignDocumentDto.java
@Data
public class DesignDocumentDto {

    private String id;
    private String pid;
    private String text;
    private Integer type;
    private String branchId;
    private String tagId;
    private String projectId;

    public DesignDocumentDto(String id,String pid, String title) {
        this.id = id;
        this.pid = pid;
        this.text = title;
    }
}

