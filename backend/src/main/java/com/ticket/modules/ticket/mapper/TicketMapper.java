package com.ticket.modules.ticket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.modules.ticket.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 工单Mapper
 *
 * @author Ticket System
 */
@Mapper
public interface TicketMapper extends BaseMapper<Ticket> {

    /**
     * 分页查询工单（带关联数据）
     */
    Page<Ticket> selectTicketPage(Page<Ticket> page, @Param("query") Ticket query);

    /**
     * 根据客户ID查询工单数量
     */
    @Select("SELECT COUNT(*) FROM ticket WHERE customer_id = #{customerId} AND deleted = 0")
    Long countByCustomerId(@Param("customerId") Long customerId);

    /**
     * 根据客户ID和状态查询工单数量
     */
    @Select("SELECT COUNT(*) FROM ticket WHERE customer_id = #{customerId} AND status = #{status} AND deleted = 0")
    Long countByCustomerIdAndStatus(@Param("customerId") Long customerId, @Param("status") String status);

    /**
     * 获取客户平均满意度评分
     */
    @Select("SELECT AVG(satisfaction_score) FROM ticket WHERE customer_id = #{customerId} AND satisfaction_score IS NOT NULL AND deleted = 0")
    Double getAvgSatisfactionScore(@Param("customerId") Long customerId);

    /**
     * 获取今日新增工单数
     */
    @Select("SELECT COUNT(*) FROM ticket WHERE DATE(create_time) = CURDATE() AND deleted = 0")
    Long countTodayNew();

    /**
     * 获取处理中工单数
     */
    @Select("SELECT COUNT(*) FROM ticket WHERE status IN ('NEW', 'CONFIRMED', 'PROCESSING', 'PENDING_VERIFY') AND deleted = 0")
    Long countProcessing();

    /**
     * 获取已解决工单数（本月）
     */
    @Select("SELECT COUNT(*) FROM ticket WHERE status = 'SOLVED' AND MONTH(solve_time) = MONTH(NOW()) AND YEAR(solve_time) = YEAR(NOW()) AND deleted = 0")
    Long countSolvedThisMonth();

    /**
     * 获取SLA预警工单数
     */
    @Select("SELECT COUNT(*) FROM ticket WHERE sla_warning = 1 AND status NOT IN ('SOLVED', 'CLOSED') AND deleted = 0")
    Long countSlaWarning();
}
