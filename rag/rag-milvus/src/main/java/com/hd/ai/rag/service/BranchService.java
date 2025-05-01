package com.hd.ai.rag.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hd.ai.rag.common.IdUtil;
import com.hd.ai.rag.entity.Branch;
import com.hd.ai.rag.mapper.BranchMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BranchService extends ServiceImpl<BranchMapper, Branch> {
    @Transactional
    public boolean del(String id) {
        return this.removeById(id);
    }
        @Transactional
        public boolean saveBranch(Branch branch){
            if (StringUtils.isBlank(branch.getId())) {
                branch.setId(IdUtil.getSnowflakeNextIdStr());
            }
            return this.saveOrUpdate(branch);
        }
}
