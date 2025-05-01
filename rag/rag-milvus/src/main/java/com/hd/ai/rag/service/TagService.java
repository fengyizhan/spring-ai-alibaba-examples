package com.hd.ai.rag.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hd.ai.rag.common.IdUtil;
import com.hd.ai.rag.entity.Project;
import com.hd.ai.rag.entity.Tag;
import com.hd.ai.rag.mapper.ProjectMapper;
import com.hd.ai.rag.mapper.TagMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService extends ServiceImpl<TagMapper, Tag> {
    @Transactional
    public boolean del(String id) {
        return this.removeById(id);
    }

    @Transactional
    public boolean saveTag(Tag tag) {
        if (StringUtils.isBlank(tag.getId())) {
            tag.setId(IdUtil.getSnowflakeNextIdStr());
        }
        return this.saveOrUpdate(tag);
    }
}
