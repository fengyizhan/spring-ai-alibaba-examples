package com.hd.ai.rag.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hd.ai.rag.entity.CodingRuleDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

// CodingRuleDocumentMapper.java
@Mapper
public interface CodingRuleDocumentMapper extends BaseMapper<CodingRuleDocument> {

}
