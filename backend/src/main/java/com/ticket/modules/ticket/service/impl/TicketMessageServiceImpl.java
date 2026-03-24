package com.ticket.modules.ticket.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ticket.common.constant.SystemConstants;
import com.ticket.common.util.SecurityUtils;
import com.ticket.modules.ticket.entity.Ticket;
import com.ticket.modules.ticket.entity.TicketMessage;
import com.ticket.modules.ticket.mapper.TicketMessageMapper;
import com.ticket.modules.ticket.service.TicketMessageService;
import com.ticket.modules.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 工单消息服务实现
 *
 * @author Ticket System
 */
@Service
@RequiredArgsConstructor
public class TicketMessageServiceImpl extends ServiceImpl<TicketMessageMapper, TicketMessage>
        implements TicketMessageService {

    private final TicketService ticketService;

    @Override
    public Page<TicketMessage> pageMessage(Page<TicketMessage> page, Long ticketId, String type) {
        return ticketMapper.selectMessagePage(page, ticketId, type);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TicketMessage sendMessage(Long ticketId, String content, String messageType) {
        Ticket ticket = ticketService.getById(ticketId);
        if (ticket == null) {
            throw new RuntimeException("工单不存在");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();

        // 判断发送者类型
        String senderType = SystemConstants.SenderType.CUSTOMER;
        if (ticket.getHandlerId() != null && ticket.getHandlerId().equals(currentUserId)) {
            senderType = SystemConstants.SenderType.SUPPORT;
        }

        TicketMessage message = new TicketMessage();
        message.setTicketId(ticketId);
        message.setContent(content);
        message.setType(messageType);
        message.setSenderType(senderType);
        message.setSenderId(currentUserId);
        message.setIsRead(SystemConstants.YesNo.NO);

        save(message);

        // 如果是客服回复，更新首次响应时间
        if (SystemConstants.SenderType.SUPPORT.equals(senderType) && ticket.getFirstResponseTime() == null) {
            ticket.setFirstResponseTime(message.getCreateTime());
            ticketService.updateById(ticket);
        }

        return message;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRead(Long ticketId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return ticketMapper.markAsRead(ticketId, currentUserId) > 0;
    }
}
