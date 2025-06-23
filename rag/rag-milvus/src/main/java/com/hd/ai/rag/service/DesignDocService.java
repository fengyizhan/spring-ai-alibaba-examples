package com.hd.ai.rag.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hd.ai.rag.common.IdUtil;
import com.hd.ai.rag.common.PageData;
import com.hd.ai.rag.dto.DesignDocumentDto;
import com.hd.ai.rag.entity.DesignDocument;
import com.hd.ai.rag.mapper.DesignDocMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

// DesignDocServiceImpl.java
@Service
public class DesignDocService extends ServiceImpl<DesignDocMapper, DesignDocument> {

    @Transactional
    public boolean delDocument(String id) {
        return this.removeById(id);
    }

    @Transactional
    public boolean saveDocument(DesignDocument doc) {
        if (StringUtils.isBlank(doc.getId())) {
            doc.setId(IdUtil.getSnowflakeNextIdStr());
        }
        if (StrUtil.isBlank(doc.getPid())) {
            doc.setPid("0");
        }
        return this.saveOrUpdate(doc);
    }

    public PageData<DesignDocumentDto> getDocumentTree(String projectId, String branchId, String tagId, String title,Integer type,String beginDate,String endDate,Integer pageNo, Integer pageSize) {
        Page<DesignDocument> page = new Page<>(pageNo, pageSize);
        lambdaQuery()
                .eq(StrUtil.isNotBlank(projectId), DesignDocument::getProjectId, projectId)
                .eq(StrUtil.isNotBlank(branchId), DesignDocument::getBranchId, branchId)
                .eq(StrUtil.isNotBlank(tagId), DesignDocument::getTagId, tagId)
                .eq((type!=null && StrUtil.isNotBlank(type.toString())), DesignDocument::getType, type)
                .orderByAsc(DesignDocument::getTitle)
                .like(StrUtil.isNotBlank(title), DesignDocument::getTitle, title)
                .and(StrUtil.isNotBlank(beginDate), con->
                        {
                            con.ge(DesignDocument::getCreateDate,beginDate)
                                    .or()
                                    .ge(DesignDocument::getUpdateDate, beginDate);
                        }
                )
                .and(StrUtil.isNotBlank(endDate), con->
                        {
                            con.le(DesignDocument::getCreateDate,endDate)
                                    .or()
                                    .le(DesignDocument::getUpdateDate, endDate);
                        }
                )
                .page(page);
        PageData<DesignDocumentDto> pageData = new PageData<>();
        pageData.setTotalPages(page.getPages());
        pageData.setPageSize(pageSize);
        pageData.setPageNo(pageNo);
        pageData.setTotalCount(page.getTotal());
        List<DesignDocument> list = page.getRecords();
        List<DesignDocumentDto> documentDtos = buildTree(list);
        pageData.setList(documentDtos);
        return pageData;
    }

    /**
     * 将数据库中的结构转换为前端jstree识别的json格式返回
     *
     * @param nodes 后端的存储结构
     * @return 前端需要的结构
     */
    private List<DesignDocumentDto> buildTree(List<DesignDocument> nodes) {
        List<DesignDocumentDto> childrensNodes = new ArrayList<>();
        nodes.forEach(node -> {
            DesignDocumentDto ddd = new DesignDocumentDto(node.getId(), node.getPid(), node.getTitle());
            childrensNodes.add(ddd);
        });
        return childrensNodes;
    }
}
