package com.alibaba.cloud.ai.example.rag.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alibaba.cloud.ai.example.rag.domain.DocumentInfo;
import com.alibaba.cloud.ai.example.rag.mapper.DocumentMapper;
import com.alibaba.cloud.ai.example.rag.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, DocumentInfo> implements DocumentService {

    @Autowired
    public DocumentMapper documentMapper;

    public List<DocumentInfo> getUsersByConditions() {
        // 创建 QueryWrapper 对象
        QueryWrapper<DocumentInfo> queryWrapper = new QueryWrapper<>();
        // 添加查询条件
        queryWrapper.eq("name", "张三")
                .ge("age", 18)
                .orderByDesc("create_time");

        // 执行查询
        return documentMapper.selectList(queryWrapper);
    }

    public int updateById() {
        // 创建 UpdateWrapper 对象
        UpdateWrapper<DocumentInfo> updateWrapper = new UpdateWrapper<>();

        // 添加更新条件
        updateWrapper.eq("id", 1);

        // 创建要更新的对象
        DocumentInfo document = new DocumentInfo();
        document.setContent("ttt");

        // 执行更新
        return documentMapper.update(document, updateWrapper);
    }
}
