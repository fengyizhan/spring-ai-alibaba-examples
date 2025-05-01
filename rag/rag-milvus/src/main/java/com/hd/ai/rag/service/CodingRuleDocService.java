package com.hd.ai.rag.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hd.ai.rag.common.IdUtil;
import com.hd.ai.rag.entity.CodingRuleDocument;
import com.hd.ai.rag.mapper.CodingRuleDocumentMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CodingRuleDocService extends ServiceImpl<CodingRuleDocumentMapper, CodingRuleDocument> {
    @Transactional
    public boolean del(String id) {
        return this.removeById(id);
    }

    @Transactional
    public boolean saveRule(CodingRuleDocument doc) {
        if (StringUtils.isBlank(doc.getId())) {
            doc.setId(IdUtil.getSnowflakeNextIdStr());
        }
        return this.saveOrUpdate(doc);
    }
}
