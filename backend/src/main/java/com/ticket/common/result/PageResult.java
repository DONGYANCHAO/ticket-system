package com.ticket.common.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 *
 * @author Ticket System
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageResult<T> extends Result<List<T>> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private Long total;

    /** 当前页码 */
    private Long pageNum;

    /** 每页记录数 */
    private Long pageSize;

    /** 总页数 */
    private Long totalPages;

    /** 是否有上一页 */
    private Boolean hasPrevious;

    /** 是否有下一页 */
    private Boolean hasNext;

    public PageResult() {
        super();
    }

    public PageResult(List<T> data, Long total, Long pageNum, Long pageSize) {
        super(200, "查询成功", data);
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = (total + pageSize - 1) / pageSize;
        this.hasPrevious = pageNum > 1;
        this.hasNext = pageNum < this.totalPages;
    }

    /**
     * 创建分页结果
     */
    public static <T> PageResult<T> of(List<T> data, Long total, Long pageNum, Long pageSize) {
        return new PageResult<>(data, total, pageNum, pageSize);
    }

    /**
     * 创建空分页结果
     */
    public static <T> PageResult<T> empty(Long pageNum, Long pageSize) {
        return new PageResult<>(List.of(), 0L, pageNum, pageSize);
    }
}
