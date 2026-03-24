package com.ticket.modules.ticket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ticket.modules.ticket.entity.TicketTag;

import java.util.List;

/**
 * 工单标签服务接口
 *
 * @author Ticket System
 */
public interface TicketTagService extends IService<TicketTag> {

    /**
     * 获取所有标签
     */
    List<TicketTag> getAllTags();

    /**
     * 获取工单的所有标签
     */
    List<TicketTag> getTagsByTicketId(Long ticketId);

    /**
     * 添加工单标签
     */
    boolean addTicketTags(Long ticketId, List<Long> tagIds);

    /**
     * 移除工单标签
     */
    boolean removeTicketTags(Long ticketId, List<Long> tagIds);

    /**
     * 更新工单标签
     */
    boolean updateTicketTags(Long ticketId, List<Long> tagIds);
}
