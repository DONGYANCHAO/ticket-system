package com.ticket.modules.ticket.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ticket.common.constant.SystemConstants;
import com.ticket.modules.ticket.entity.Ticket;
import com.ticket.modules.ticket.mapper.TicketMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * SLA定时任务
 *
 * @author Ticket System
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SlaTask {

    private final TicketMapper ticketMapper;

    /**
     * 检查SLA超时（每5分钟执行一次）
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void checkSlaTimeout() {
        log.info("开始检查SLA超时...");

        // 查询所有处理中的工单
        List<Ticket> processingTickets = ticketMapper.selectList(
                new LambdaQueryWrapper<Ticket>()
                        .in(Ticket::getStatus,
                                SystemConstants.TicketStatus.NEW,
                                SystemConstants.TicketStatus.CONFIRMED,
                                SystemConstants.TicketStatus.PROCESSING,
                                SystemConstants.TicketStatus.PENDING_VERIFY)
                        .eq(Ticket::getDeleted, 0)
        );

        int warningCount = 0;

        for (Ticket ticket : processingTickets) {
            LocalDateTime now = LocalDateTime.now();
            boolean isWarning = false;

            // 检查首次响应时限
            if (ticket.getFirstResponseTime() == null && ticket.getSlaFirstResponseMinutes() != null) {
                long minutesSinceCreate = ChronoUnit.MINUTES.between(ticket.getCreateTime(), now);
                if (minutesSinceCreate >= ticket.getSlaFirstResponseMinutes() * 0.8) {
                    isWarning = true;
                }
            }

            // 检查解决时限
            if (ticket.getSlaResolveMinutes() != null) {
                long minutesSinceCreate = ChronoUnit.MINUTES.between(ticket.getCreateTime(), now);
                if (minutesSinceCreate >= ticket.getSlaResolveMinutes() * 0.8) {
                    isWarning = true;
                }
            }

            // 更新SLA预警状态
            if (isWarning && ticket.getSlaWarning() != 1) {
                ticket.setSlaWarning(1);
                ticketMapper.updateById(ticket);
                warningCount++;
                log.warn("工单SLA预警: {}, 已创建{}分钟", ticket.getTicketNo(),
                        ChronoUnit.MINUTES.between(ticket.getCreateTime(), now));
            }
        }

        log.info("SLA超时检查完成，发现{}个预警工单", warningCount);
    }
}
