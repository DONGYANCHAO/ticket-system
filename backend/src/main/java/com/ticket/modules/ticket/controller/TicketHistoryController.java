package com.ticket.modules.ticket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.common.result.Result;
import com.ticket.modules.ticket.entity.TicketHistory;
import com.ticket.modules.ticket.service.TicketHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 工单历史控制器
 *
 * @author Ticket System
 */
@Tag(name = "工单历史管理")
@RestController
@RequestMapping("/api/ticket/{ticketId}/history")
@RequiredArgsConstructor
public class TicketHistoryController {

    private final TicketHistoryService historyService;

    /**
     * 获取工单历史
     */
    @Operation(summary = "获取工单历史")
    @GetMapping
    public Result<Page<TicketHistory>> list(
            @PathVariable Long ticketId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Long pageSize
    ) {
        return Result.success(historyService.pageHistory(
                new Page<>(page, pageSize), ticketId
        ));
    }
}
