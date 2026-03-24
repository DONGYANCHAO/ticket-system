package com.ticket.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.modules.system.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 操作日志Mapper
 *
 * @author Ticket System
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    /**
     * 分页查询操作日志
     */
    Page<OperationLog> selectLogPage(Page<OperationLog> page, @Param("query") OperationLog query);
}
