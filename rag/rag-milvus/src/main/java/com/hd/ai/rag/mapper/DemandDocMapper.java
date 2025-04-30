package com.hd.ai.rag.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hd.ai.rag.entity.DemandDocument;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DemandDocMapper extends BaseMapper<DemandDocument> {
}
