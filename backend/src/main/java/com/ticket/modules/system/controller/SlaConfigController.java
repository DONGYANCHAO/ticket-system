package com.ticket.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.common.annotation.Log;
import com.ticket.common.constant.SystemConstants;
import com.ticket.common.result.Result;
import com.ticket.modules.system.entity.SlaConfig;
import com.ticket.modules.system.service.SlaConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * SLA配置控制器
 *
 * @author Ticket System
 */
@Tag(name = "SLA配置管理")
@RestController
@RequestMapping("/api/system/sla")
@RequiredArgsConstructor
public class SlaConfigController {

    private final SlaConfigService slaConfigService;

    /**
     * 获取SLA配置分页列表
     */
    @Operation(summary = "获取SLA配置分页列表")
    @GetMapping("/list")
    public Result<Page<SlaConfig>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long pageSize
    ) {
        return Result.success(slaConfigService.pageSla(new Page<>(page, pageSize)));
    }

    /**
     * 获取所有SLA配置
     */
    @Operation(summary = "获取所有SLA配置")
    @GetMapping("/all")
    public Result<?> getAllSla() {
        return Result.success(slaConfigService.list(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SlaConfig>()
                        .eq(SlaConfig::getStatus, SystemConstants.Status.ENABLED)
                        .orderByAsc(SlaConfig::getLevel)
        ));
    }

    /**
     * 获取SLA配置详情
     */
    @Operation(summary = "获取SLA配置详情")
    @GetMapping("/{id}")
    public Result<SlaConfig> detail(@PathVariable Long id) {
        return Result.success(slaConfigService.getById(id));
    }

    /**
     * 新增SLA配置
     */
    @Operation(summary = "新增SLA配置")
    @Log(module = SystemConstants.LogModule.SYSTEM, content = "新增SLA配置")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody SlaConfig slaConfig) {
        slaConfigService.save(slaConfig);
        return Result.success();
    }

    /**
     * 更新SLA配置
     */
    @Operation(summary = "更新SLA配置")
    @Log(module = SystemConstants.LogModule.SYSTEM, content = "更新SLA配置")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SlaConfig slaConfig) {
        slaConfig.setId(id);
        slaConfigService.updateById(slaConfig);
        return Result.success();
    }

    /**
     * 删除SLA配置
     */
    @Operation(summary = "删除SLA配置")
    @Log(module = SystemConstants.LogModule.SYSTEM, content = "删除SLA配置")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        slaConfigService.removeById(id);
        return Result.success();
    }
}
