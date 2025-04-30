package com.hd.ai.rag.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("documents")
@Data
public class DocumentInfo {
    @TableId(type = IdType.NONE)
    private int id;
    private String content;
}
