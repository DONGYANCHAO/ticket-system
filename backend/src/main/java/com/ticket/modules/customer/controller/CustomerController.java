package com.ticket.modules.customer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.common.annotation.Log;
import com.ticket.common.constant.SystemConstants;
import com.ticket.common.result.Result;
import com.ticket.modules.customer.entity.Customer;
import com.ticket.modules.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 客户控制器
 *
 * @author Ticket System
 */
@Tag(name = "客户管理")
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 获取客户分页列表
     */
    @Operation(summary = "获取客户分页列表")
    @GetMapping("/list")
    public Result<Page<Customer>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "客户名称") @RequestParam(required = false) String name,
            @Parameter(description = "客户等级") @RequestParam(required = false) String level,
            @Parameter(description = "所属行业") @RequestParam(required = false) String industry,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status
    ) {
        Customer query = new Customer();
        query.setName(name);
        query.setLevel(level);
        query.setIndustry(industry);
        query.setStatus(status);

        Page<Customer> result = customerService.pageCustomer(
                new Page<>(page, pageSize), query
        );
        return Result.success(result);
    }

    /**
     * 获取客户详情
     */
    @Operation(summary = "获取客户详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        return Result.success(customerService.getCustomerDetail(id));
    }

    /**
     * 新增客户
     */
    @Operation(summary = "新增客户")
    @Log(module = SystemConstants.LogModule.CUSTOMER, content = "新增客户")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody Customer customer) {
        customerService.addCustomer(customer);
        return Result.success();
    }

    /**
     * 更新客户
     */
    @Operation(summary = "更新客户")
    @Log(module = SystemConstants.LogModule.CUSTOMER, content = "更新客户")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody Customer customer) {
        customer.setId(id);
        customerService.updateCustomer(customer);
        return Result.success();
    }

    /**
     * 删除客户
     */
    @Operation(summary = "删除客户")
    @Log(module = SystemConstants.LogModule.CUSTOMER, content = "删除客户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return Result.success();
    }

    /**
     * 启用/禁用客户
     */
    @Operation(summary = "启用/禁用客户")
    @Log(module = SystemConstants.LogModule.CUSTOMER, content = "修改客户状态")
    @PostMapping("/{id}/status")
    public Result<Void> changeStatus(
            @PathVariable Long id,
            @RequestParam Integer status
    ) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setStatus(status);
        customerService.updateById(customer);
        return Result.success();
    }

    /**
     * 获取客户统计信息
     */
    @Operation(summary = "获取客户统计信息")
    @GetMapping("/{id}/statistics")
    public Result<Map<String, Object>> statistics(@PathVariable Long id) {
        return Result.success(customerService.getCustomerStatistics(id));
    }

    /**
     * 获取所有客户（简单列表）
     */
    @Operation(summary = "获取所有客户")
    @GetMapping("/all")
    public Result<?> getAllCustomers(
            @Parameter(description = "状态") @RequestParam(required = false) Integer status
    ) {
        return Result.success(customerService.list(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Customer>()
                        .eq(status != null, Customer::getStatus, status)
                        .eq(Customer::getDeleted, 0)
                        .orderByAsc(Customer::getName)
        ));
    }
}
