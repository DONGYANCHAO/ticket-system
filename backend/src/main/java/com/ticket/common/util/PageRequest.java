package com.ticket.common.util;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求参数
 *
 * @author Ticket System
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页码（从1开始） */
    private Long pageNum = 1L;

    /** 每页记录数 */
    private Long pageSize = 10L;

    /** 最大每页记录数 */
    private static final Long MAX_PAGE_SIZE = 100L;

    /**
     * 获取偏移量
     */
    public Long getOffset() {
        return (pageNum - 1) * pageSize;
    }

    /**
     * 校验并修复分页参数
     */
    public void validate() {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1L;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10L;
        }
        if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }
    }
}
