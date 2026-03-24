package com.ticket.modules.ticket.controller;

import com.ticket.common.result.Result;
import com.ticket.modules.ticket.entity.TicketTag;
import com.ticket.modules.ticket.service.TicketTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工单标签控制器
 *
 * @author Ticket System
 */
@Tag(name = "工单标签管理")
@RestController
@RequestMapping("/api/ticket/tag")
@RequiredArgsConstructor
public class TicketTagController {

    private final TicketTagService tagService;

    /**
     * 获取所有标签
     */
    @Operation(summary = "获取所有标签")
    @GetMapping("/all")
    public Result<List<TicketTag>> getAllTags() {
        return Result.success(tagService.getAllTags());
    }

    /**
     * 获取工单的标签
     */
    @Operation(summary = "获取工单的标签")
    @GetMapping("/ticket/{ticketId}")
    public Result<List<TicketTag>> getTicketTags(@PathVariable Long ticketId) {
        return Result.success(tagService.getTagsByTicketId(ticketId));
    }

    /**
     * 新增标签
     */
    @Operation(summary = "新增标签")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody TicketTag tag) {
        tagService.save(tag);
        return Result.success();
    }

    /**
     * 更新标签
     */
    @Operation(summary = "更新标签")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TicketTag tag) {
        tag.setId(id);
        tagService.updateById(tag);
        return Result.success();
    }

    /**
     * 删除标签
     */
    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tagService.removeById(id);
        return Result.success();
    }
}
