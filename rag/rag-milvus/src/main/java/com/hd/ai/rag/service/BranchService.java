package com.hd.ai.rag.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hd.ai.rag.common.IdUtil;
import com.hd.ai.rag.entity.Branch;
import com.hd.ai.rag.mapper.BranchMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BranchService extends ServiceImpl<BranchMapper, Branch> {
    @Transactional
    public boolean del(String id) {
        return this.removeById(id);
    }

    @Transactional
    public boolean saveBranch(Branch branch) {
        if (StringUtils.isBlank(branch.getId())) {
            branch.setId(IdUtil.getSnowflakeNextIdStr());
        }
        return this.saveOrUpdate(branch);
    }

    public List<Branch> list(String title, Integer currentPage, Integer pageSize) {
        Page<Branch> page = new Page<>(currentPage, pageSize);
        List<Branch> list = lambdaQuery()
                .and(StrUtil.isNotBlank(title), con ->
                        {
                            con.like(Branch::getTitle, title)
                                    .or()
                                    .like(Branch::getNote, title);
                        }
                )
                .orderByDesc(Branch::getCreateDate)
                .page(page).getRecords();
        return list;
    }

    public List<Branch> listAll() {
        List<Branch> list = this.list();
        return list;
    }
}
