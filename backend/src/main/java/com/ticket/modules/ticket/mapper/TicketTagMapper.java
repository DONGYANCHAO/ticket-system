package com.ticket.modules.ticket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ticket.modules.ticket.entity.TicketTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工单标签Mapper
 *
 * @author Ticket System
 */
@Mapper
public interface TicketTagMapper extends BaseMapper<TicketTag> {

    /**
     * 获取工单的所有标签
     */
    List<TicketTag> selectTagsByTicketId(@Param("ticketId") Long ticketId);

    /**
     * 批量添加工单标签关联
     */
    int batchInsertTicketTags(@Param("ticketId") Long ticketId, @Param("tagIds") List<Long> tagIds);

    /**
     * 删除工单的所有标签关联
     */
    int deleteTicketTags(@Param("ticketId") Long ticketId);
}
