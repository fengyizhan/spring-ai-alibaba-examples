package com.hd.ai.rag.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// DemandDocument.java
@Data
@TableName("demand_document")
@Accessors(chain = true)
public class DemandDocument {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @TableField("title")
    private String title;

    @TableField("pid")
    private String pid;

    @TableField(value = "content", jdbcType = JdbcType.LONGVARCHAR)
    private String content;

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
}
