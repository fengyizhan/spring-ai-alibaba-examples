package com.hd.ai.rag.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// DesignDocument.java
@Data
@TableName("design_document")
@Accessors(chain = true)
public class DesignDocument {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @TableField("title")
    private String title;

    @TableField("pid")
    private String pid;

    @TableField("content")
    private String content;

    @TableField("order_number")
    private Integer orderNumber;

    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    @TableField(value = "update_date", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateDate;

    @TableField("branch_id")
    private String branchId;
    @TableField("tag_id")
    private String tagId;
    @TableField("project_id")
    private String projectId;
    private List<DesignDocument> children=new ArrayList<>();
}
