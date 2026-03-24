package com.ticket.modules.ticket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ticket.modules.ticket.entity.TicketTag;
import com.ticket.modules.ticket.mapper.TicketTagMapper;
import com.ticket.modules.ticket.service.TicketTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 工单标签服务实现
 *
 * @author Ticket System
 */
@Service
@RequiredArgsConstructor
public class TicketTagServiceImpl extends ServiceImpl<TicketTagMapper, TicketTag>
        implements TicketTagService {

    private final TicketTagMapper ticketTagMapper;

    @Override
    public List<TicketTag> getAllTags() {
        return list();
    }

    @Override
    public List<TicketTag> getTagsByTicketId(Long ticketId) {
        return ticketTagMapper.selectTagsByTicketId(ticketId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTicketTags(Long ticketId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return true;
        }
        return ticketTagMapper.batchInsertTicketTags(ticketId, tagIds) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeTicketTags(Long ticketId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return true;
        }
        // 删除指定标签关联
        return removeByMap(java.util.Map.of("ticket_id", ticketId)) ||
               ticketTagMapper.deleteTicketTags(ticketId) >= 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTicketTags(Long ticketId, List<Long> tagIds) {
        // 先删除所有标签
        ticketTagMapper.deleteTicketTags(ticketId);
        // 再添加新标签
        if (tagIds != null && !tagIds.isEmpty()) {
            ticketTagMapper.batchInsertTicketTags(ticketId, tagIds);
        }
        return true;
    }
}
