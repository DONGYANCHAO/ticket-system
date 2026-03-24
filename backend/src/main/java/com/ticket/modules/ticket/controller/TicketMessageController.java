package com.ticket.modules.ticket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.common.annotation.Log;
import com.ticket.common.constant.SystemConstants;
import com.ticket.common.result.Result;
import com.ticket.modules.ticket.entity.TicketMessage;
import com.ticket.modules.ticket.service.TicketMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 工单消息控制器
 *
 * @author Ticket System
 */
@Tag(name = "工单消息管理")
@RestController
@RequestMapping("/api/ticket/{ticketId}/messages")
@RequiredArgsConstructor
public class TicketMessageController {

    private final TicketMessageService messageService;

    /**
     * 获取工单消息列表
     */
    @Operation(summary = "获取工单消息列表")
    @GetMapping
    public Result<Page<TicketMessage>> list(
            @PathVariable Long ticketId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Long pageSize,
            @Parameter(description = "消息类型") @RequestParam(required = false) String type
    ) {
        return Result.success(messageService.pageMessage(
                new Page<>(page, pageSize), ticketId, type
        ));
    }

    /**
     * 发送消息
     */
    @Operation(summary = "发送消息")
    @Log(module = SystemConstants.LogModule.TICKET, content = "发送工单消息")
    @PostMapping
    public Result<TicketMessage> send(
            @PathVariable Long ticketId,
            @RequestBody TicketMessage message
    ) {
        return Result.success(messageService.sendMessage(ticketId, message.getContent(), message.getType()));
    }

    /**
     * 标记消息已读
     */
    @Operation(summary = "标记消息已读")
    @PostMapping("/read")
    public Result<Void> markAsRead(@PathVariable Long ticketId) {
        messageService.markAsRead(ticketId);
        return Result.success();
    }
}
