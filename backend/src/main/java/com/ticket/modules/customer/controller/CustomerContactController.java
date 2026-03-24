package com.ticket.modules.customer.controller;

import com.ticket.common.annotation.Log;
import com.ticket.common.constant.SystemConstants;
import com.ticket.common.result.Result;
import com.ticket.modules.customer.entity.CustomerContact;
import com.ticket.modules.customer.service.CustomerContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户联系人控制器
 *
 * @author Ticket System
 */
@Tag(name = "客户联系人管理")
@RestController
@RequestMapping("/api/customer/{customerId}/contacts")
@RequiredArgsConstructor
public class CustomerContactController {

    private final CustomerContactService contactService;

    /**
     * 获取客户联系人列表
     */
    @Operation(summary = "获取客户联系人列表")
    @GetMapping
    public Result<List<CustomerContact>> list(@PathVariable Long customerId) {
        return Result.success(contactService.getContactsByCustomerId(customerId));
    }

    /**
     * 新增联系人
     */
    @Operation(summary = "新增联系人")
    @Log(module = SystemConstants.LogModule.CUSTOMER, content = "新增客户联系人")
    @PostMapping
    public Result<Void> add(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerContact contact
    ) {
        contact.setCustomerId(customerId);
        contactService.addContact(contact);
        return Result.success();
    }

    /**
     * 更新联系人
     */
    @Operation(summary = "更新联系人")
    @Log(module = SystemConstants.LogModule.CUSTOMER, content = "更新客户联系人")
    @PutMapping("/{id}")
    public Result<Void> update(
            @PathVariable Long customerId,
            @PathVariable Long id,
            @Valid @RequestBody CustomerContact contact
    ) {
        contact.setId(id);
        contact.setCustomerId(customerId);
        contactService.updateContact(contact);
        return Result.success();
    }

    /**
     * 删除联系人
     */
    @Operation(summary = "删除联系人")
    @Log(module = SystemConstants.LogModule.CUSTOMER, content = "删除客户联系人")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        contactService.deleteContact(id);
        return Result.success();
    }
}
