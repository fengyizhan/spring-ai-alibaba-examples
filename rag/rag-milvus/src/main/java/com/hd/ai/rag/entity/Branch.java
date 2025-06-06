package com.hd.ai.rag.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

// Branch.java
@Data
@TableName("branch")
public class Branch {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    @TableField("title")
    private String title;
    @TableField("note")
    private String note;
    @TableField("order_number")
    private Integer orderNumber;
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    @TableField(value = "update_date", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateDate;
}
