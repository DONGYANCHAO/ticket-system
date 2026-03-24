package com.ticket.modules.ticket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ticket.modules.ticket.dto.TicketCreateRequest;
import com.ticket.modules.ticket.dto.TicketUpdateRequest;
import com.ticket.modules.ticket.entity.Ticket;

import java.util.Map;

/**
 * 工单服务接口
 *
 * @author Ticket System
 */
public interface TicketService extends IService<Ticket> {

    /**
     * 分页查询工单
     */
    Page<Ticket> pageTicket(Page<Ticket> page, Ticket query);

    /**
     * 获取工单详情
     */
    Map<String, Object> getTicketDetail(Long id);

    /**
     * 创建工单
     */
    Map<String, Object> createTicket(TicketCreateRequest request);

    /**
     * 更新工单
     */
    boolean updateTicket(Long id, TicketUpdateRequest request);

    /**
     * 删除工单
     */
    boolean deleteTicket(Long id);

    /**
     * 分配工单
     */
    boolean assignTicket(Long id, Long handlerId, String remark);

    /**
     * 关闭工单
     */
    boolean closeTicket(Long id, String reason);

    /**
     * 撤回工单
     */
    boolean withdrawTicket(Long id);

    /**
     * 处理工单（开始处理）
     */
    boolean startProcess(Long id);

    /**
     * 完成处理（待验证）
     */
    boolean pendingVerify(Long id, String remark);

    /**
     * 解决工单
     */
    boolean solveTicket(Long id, String remark);

    /**
     * 合并工单
     */
    boolean mergeTickets(Long mainId, Long... mergeIds);

    /**
     * 拆分工单
     */
    boolean splitTicket(Long id, TicketCreateRequest request);

    /**
     * 催单
     */
    boolean remindTicket(Long id);

    /**
     * 获取我的待办工单
     */
    Page<Ticket> getTodoTickets(Page<Ticket> page);

    /**
     * 获取我创建的工单
     */
    Page<Ticket> getMyCreatedTickets(Page<Ticket> page);

    /**
     * 获取仪表盘统计
     */
    Map<String, Object> getDashboardStatistics();
}
