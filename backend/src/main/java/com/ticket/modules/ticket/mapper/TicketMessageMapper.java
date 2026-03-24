package com.ticket.modules.ticket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.modules.ticket.entity.TicketMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 工单消息Mapper
 *
 * @author Ticket System
 */
@Mapper
public interface TicketMessageMapper extends BaseMapper<TicketMessage> {

    /**
     * 分页查询工单消息
     */
    Page<TicketMessage> selectMessagePage(Page<TicketMessage> page, @Param("ticketId") Long ticketId, @Param("type") String type);

    /**
     * 标记消息已读
     */
    int markAsRead(@Param("ticketId") Long ticketId, @Param("receiverId") Long receiverId);
}
