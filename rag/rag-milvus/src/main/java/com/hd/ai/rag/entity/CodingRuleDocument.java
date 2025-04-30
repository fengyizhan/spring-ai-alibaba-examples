package com.hd.ai.rag.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
@Data
@TableName("coding_rule_document")
@Accessors(chain = true)
public class CodingRuleDocument {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @TableField("title")
    private String title;


    @TableField("content")
    private String content;

    @TableField("project_id")
    private String projectId;

    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    @TableField(value = "update_date", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateDate;
}
