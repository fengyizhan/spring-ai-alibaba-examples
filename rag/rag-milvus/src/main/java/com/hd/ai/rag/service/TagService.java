package com.hd.ai.rag.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hd.ai.rag.common.IdUtil;
import com.hd.ai.rag.entity.Tag;
import com.hd.ai.rag.mapper.TagMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<Tag> list(String title, Integer currentPage, Integer pageSize) {
        Page<Tag> page = new Page<>(currentPage, pageSize);
        List<Tag> list = lambdaQuery()
                .and(StrUtil.isNotBlank(title), con ->
                        {
                            con.like(Tag::getTitle, title)
                                    .or()
                                    .like(Tag::getNote, title);
                        }
                )
                .orderByDesc(Tag::getCreateDate)
                .page(page).getRecords();
        return list;
    }

    public List<Tag> listAll() {
        List<Tag> list = this.list();
        return list;
    }
}
