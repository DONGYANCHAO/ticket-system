package com.ticket.modules.ticket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.common.annotation.Log;
import com.ticket.common.constant.SystemConstants;
import com.ticket.common.result.Result;
import com.ticket.modules.ticket.dto.TicketCreateRequest;
import com.ticket.modules.ticket.dto.TicketUpdateRequest;
import com.ticket.modules.ticket.entity.Ticket;
import com.ticket.modules.ticket.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 工单控制器
 *
 * @author Ticket System
 */
@Tag(name = "工单管理")
@RestController
@RequestMapping("/api/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /**
     * 获取工单分页列表
     */
    @Operation(summary = "获取工单分页列表")
    @GetMapping("/list")
    public Result<Page<Ticket>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "工单编号") @RequestParam(required = false) String ticketNo,
            @Parameter(description = "标题") @RequestParam(required = false) String title,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "类型") @RequestParam(required = false) String type,
            @Parameter(description = "优先级") @RequestParam(required = false) String priority,
            @Parameter(description = "客户ID") @RequestParam(required = false) Long customerId,
            @Parameter(description = "处理人ID") @RequestParam(required = false) Long handlerId
    ) {
        Ticket query = new Ticket();
        query.setTicketNo(ticketNo);
        query.setTitle(title);
        query.setStatus(status);
        query.setType(type);
        query.setPriority(priority);
        query.setCustomerId(customerId);
        query.setHandlerId(handlerId);

        Page<Ticket> result = ticketService.pageTicket(
                new Page<>(page, pageSize), query
        );
        return Result.success(result);
    }

    /**
     * 获取工单详情
     */
    @Operation(summary = "获取工单详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        return Result.success(ticketService.getTicketDetail(id));
    }

    /**
     * 创建工单
     */
    @Operation(summary = "创建工单")
    @Log(module = SystemConstants.LogModule.TICKET, content = "创建工单")
    @PostMapping
    public Result<Map<String, Object>> create(@Valid @RequestBody TicketCreateRequest request) {
        return Result.success(ticketService.createTicket(request));
    }

    /**
     * 更新工单
     */
    @Operation(summary = "更新工单")
    @Log(module = SystemConstants.LogModule.TICKET, content = "更新工单")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TicketUpdateRequest request) {
        ticketService.updateTicket(id, request);
        return Result.success();
    }

    /**
     * 删除工单
     */
    @Operation(summary = "删除工单")
    @Log(module = SystemConstants.LogModule.TICKET, content = "删除工单")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return Result.success();
    }

    /**
     * 分配工单
     */
    @Operation(summary = "分配工单")
    @Log(module = SystemConstants.LogModule.TICKET, content = "分配工单")
    @PostMapping("/{id}/assign")
    public Result<Void> assign(
            @PathVariable Long id,
            @RequestParam Long handlerId,
            @RequestParam(required = false) String remark
    ) {
        ticketService.assignTicket(id, handlerId, remark);
        return Result.success();
    }

    /**
     * 关闭工单
     */
    @Operation(summary = "关闭工单")
    @Log(module = SystemConstants.LogModule.TICKET, content = "关闭工单")
    @PostMapping("/{id}/close")
    public Result<Void> close(
            @PathVariable Long id,
            @RequestParam(required = false) String reason
    ) {
        ticketService.closeTicket(id, reason);
        return Result.success();
    }

    /**
     * 撤回工单
     */
    @Operation(summary = "撤回工单")
    @Log(module = SystemConstants.LogModule.TICKET, content = "撤回工单")
    @PostMapping("/{id}/withdraw")
    public Result<Void> withdraw(@PathVariable Long id) {
        ticketService.withdrawTicket(id);
        return Result.success();
    }

    /**
     * 开始处理
     */
    @Operation(summary = "开始处理工单")
    @Log(module = SystemConstants.LogModule.TICKET, content = "开始处理工单")
    @PostMapping("/{id}/start")
    public Result<Void> startProcess(@PathVariable Long id) {
        ticketService.startProcess(id);
        return Result.success();
    }

    /**
     * 待验证
     */
    @Operation(summary = "标记为待验证")
    @Log(module = SystemConstants.LogModule.TICKET, content = "标记工单待验证")
    @PostMapping("/{id}/pending-verify")
    public Result<Void> pendingVerify(
            @PathVariable Long id,
            @RequestParam(required = false) String remark
    ) {
        ticketService.pendingVerify(id, remark);
        return Result.success();
    }

    /**
     * 解决工单
     */
    @Operation(summary = "解决工单")
    @Log(module = SystemConstants.LogModule.TICKET, content = "解决工单")
    @PostMapping("/{id}/solve")
    public Result<Void> solve(
            @PathVariable Long id,
            @RequestParam(required = false) String remark
    ) {
        ticketService.solveTicket(id, remark);
        return Result.success();
    }

    /**
     * 合并工单
     */
    @Operation(summary = "合并工单")
    @Log(module = SystemConstants.LogModule.TICKET, content = "合并工单")
    @PostMapping("/{id}/merge")
    public Result<Void> merge(
            @PathVariable Long id,
            @RequestParam Long... mergeIds
    ) {
        ticketService.mergeTickets(id, mergeIds);
        return Result.success();
    }

    /**
     * 拆分工单
     */
    @Operation(summary = "拆分工单")
    @Log(module = SystemConstants.LogModule.TICKET, content = "拆分工单")
    @PostMapping("/{id}/split")
    public Result<Void> split(
            @PathVariable Long id,
            @Valid @RequestBody TicketCreateRequest request
    ) {
        ticketService.splitTicket(id, request);
        return Result.success();
    }

    /**
     * 催单
     */
    @Operation(summary = "催单")
    @Log(module = SystemConstants.LogModule.TICKET, content = "催单")
    @PostMapping("/{id}/remind")
    public Result<Void> remind(@PathVariable Long id) {
        ticketService.remindTicket(id);
        return Result.success();
    }

    /**
     * 获取我的待办工单
     */
    @Operation(summary = "获取我的待办工单")
    @GetMapping("/todo")
    public Result<Page<Ticket>> todo(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long pageSize
    ) {
        return Result.success(ticketService.getTodoTickets(new Page<>(page, pageSize)));
    }

    /**
     * 获取我创建的工单
     */
    @Operation(summary = "获取我创建的工单")
    @GetMapping("/my/created")
    public Result<Page<Ticket>> myCreated(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long pageSize
    ) {
        return Result.success(ticketService.getMyCreatedTickets(new Page<>(page, pageSize)));
    }

    /**
     * 获取仪表盘统计
     */
    @Operation(summary = "获取仪表盘统计")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        return Result.success(ticketService.getDashboardStatistics());
    }
}
