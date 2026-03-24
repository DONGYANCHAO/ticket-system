package com.ticket.modules.ticket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.modules.ticket.entity.TicketHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 工单变更日志Mapper
 *
 * @author Ticket System
 */
@Mapper
public interface TicketHistoryMapper extends BaseMapper<TicketHistory> {

    /**
     * 分页查询工单变更日志
     */
    Page<TicketHistory> selectHistoryPage(Page<TicketHistory> page, @Param("ticketId") Long ticketId);
}
