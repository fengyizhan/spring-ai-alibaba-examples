package com.alibaba.cloud.ai.example.rag.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.alibaba.cloud.ai.example.rag.domain.DocumentInfo;

@Mapper
public interface DocumentMapper extends BaseMapper<DocumentInfo> {
}
