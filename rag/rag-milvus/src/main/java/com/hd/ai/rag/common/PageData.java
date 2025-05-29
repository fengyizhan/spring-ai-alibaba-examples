package com.hd.ai.rag.common;

import lombok.Data;

import java.util.List;

/**
 * 前端需要使用的分页对象
 */
@Data
public class PageData<T> {
    private Integer pageNo;
    private Integer pageSize;
    private Long totalCount;
    private Long totalPages;
    private List<T> list;
}
