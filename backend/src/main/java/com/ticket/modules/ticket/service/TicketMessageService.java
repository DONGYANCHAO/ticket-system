package com.ticket.modules.ticket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ticket.modules.ticket.entity.TicketMessage;

/**
 * 工单消息服务接口
 *
 * @author Ticket System
 */
public interface TicketMessageService extends IService<TicketMessage> {

    /**
     * 分页查询工单消息
     */
    Page<TicketMessage> pageMessage(Page<TicketMessage> page, Long ticketId, String type);

    /**
     * 发送消息
     */
    TicketMessage sendMessage(Long ticketId, String content, String type);

    /**
     * 标记消息已读
     */
    boolean markAsRead(Long ticketId);
}
