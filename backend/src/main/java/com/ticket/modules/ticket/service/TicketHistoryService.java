package com.ticket.modules.ticket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ticket.modules.ticket.entity.TicketHistory;

/**
 * 工单历史服务接口
 *
 * @author Ticket System
 */
public interface TicketHistoryService extends IService<TicketHistory> {

    /**
     * 分页查询工单历史
     */
    Page<TicketHistory> pageHistory(Page<TicketHistory> page, Long ticketId);

    /**
     * 记录操作日志
     */
    void recordHistory(Long ticketId, String action, String content, String oldValue, String newValue);

    /**
     * 记录状态变更
     */
    void recordStatusChange(Long ticketId, String oldStatus, String newStatus);

    /**
     * 记录分配
     */
    void recordAssign(Long ticketId, Long oldHandlerId, Long newHandlerId);

    /**
     * 记录回复
     */
    void recordReply(Long ticketId, Long senderId, String senderType, String type);
}
