package com.hd.ai.rag.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hd.ai.rag.common.IdUtil;
import com.hd.ai.rag.common.PageData;
import com.hd.ai.rag.dto.DemandDocumentDto;
import com.hd.ai.rag.entity.DemandDocument;
import com.hd.ai.rag.mapper.DemandDocMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

// DemandDocServiceImpl.java
@Service
public class DemandDocService extends ServiceImpl<DemandDocMapper, DemandDocument> {

    @Transactional
    public boolean delDocument(String id) {
        return this.removeById(id);
    }

    @Transactional
    public boolean saveDocument(DemandDocument doc) {
        if (StringUtils.isBlank(doc.getId())) {
            doc.setId(IdUtil.getSnowflakeNextIdStr());
        }
        if (StrUtil.isBlank(doc.getPid())) {
            doc.setPid("0");
        }
        return this.saveOrUpdate(doc);
    }

    public PageData<DemandDocumentDto> getDocumentTree(String projectId, String branchId, String tagId, String title,String beginDate,String endDate,Integer pageNo, Integer pageSize) {
        Page<DemandDocument> page = new Page<>(pageNo, pageSize);
        lambdaQuery()
                .eq(StrUtil.isNotBlank(projectId), DemandDocument::getProjectId, projectId)
                .eq(StrUtil.isNotBlank(branchId), DemandDocument::getBranchId, branchId)
                .eq(StrUtil.isNotBlank(tagId), DemandDocument::getTagId, tagId)
                .orderByAsc(DemandDocument::getTitle)
                .like(StrUtil.isNotBlank(title), DemandDocument::getTitle, title)
                .and(StrUtil.isNotBlank(beginDate), con->
                        {
                            con.ge(DemandDocument::getCreateDate,beginDate)
                                    .or()
                                    .ge(DemandDocument::getUpdateDate, beginDate);
                        }
                )
                .and(StrUtil.isNotBlank(endDate), con->
                        {
                            con.le(DemandDocument::getCreateDate,endDate)
                                    .or()
                                    .le(DemandDocument::getUpdateDate, endDate);
                        }
                )
                .page(page);
        PageData<DemandDocumentDto> pageData = new PageData<>();
        pageData.setTotalPages(page.getPages());
        pageData.setPageSize(pageSize);
        pageData.setPageNo(pageNo);
        pageData.setTotalCount(page.getTotal());
        List<DemandDocument> list = page.getRecords();
        List<DemandDocumentDto> documentDtos = buildTree(list);
        pageData.setList(documentDtos);
        return pageData;
    }

    /**
     * 将数据库中的结构转换为前端jstree识别的json格式返回
     *
     * @param nodes 后端的存储结构
     * @return 前端需要的结构
     */
    private List<DemandDocumentDto> buildTree(List<DemandDocument> nodes) {
        List<DemandDocumentDto> childrensNodes = new ArrayList<>();
        nodes.forEach(node -> {
            DemandDocumentDto ddd = new DemandDocumentDto(node.getId(), node.getPid(), node.getTitle());
            childrensNodes.add(ddd);
        });
        return childrensNodes;
    }
}
