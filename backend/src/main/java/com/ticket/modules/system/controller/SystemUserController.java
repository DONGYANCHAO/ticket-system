package com.ticket.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.common.annotation.Log;
import com.ticket.common.constant.SystemConstants;
import com.ticket.common.result.Result;
import com.ticket.modules.system.entity.SystemUser;
import com.ticket.modules.system.service.SystemUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统用户控制器
 *
 * @author Ticket System
 */
@Tag(name = "系统用户管理")
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class SystemUserController {

    private final SystemUserService userService;

    /**
     * 获取用户分页列表
     */
    @Operation(summary = "获取用户分页列表")
    @GetMapping("/list")
    public Result<Page<SystemUser>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "姓名") @RequestParam(required = false) String realName,
            @Parameter(description = "角色") @RequestParam(required = false) String role,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status
    ) {
        SystemUser query = new SystemUser();
        query.setUsername(username);
        query.setRealName(realName);
        query.setRole(role);
        query.setStatus(status);

        Page<SystemUser> result = userService.pageUser(
                new Page<>(page, pageSize), query
        );
        return Result.success(result);
    }

    /**
     * 获取用户详情
     */
    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<SystemUser> detail(@PathVariable Long id) {
        SystemUser user = userService.getById(id);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 新增用户
     */
    @Operation(summary = "新增用户")
    @Log(module = SystemConstants.LogModule.SYSTEM, content = "新增用户")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody SystemUser user) {
        // 设置默认密码
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("123456");
        }
        userService.addUser(user);
        return Result.success();
    }

    /**
     * 更新用户
     */
    @Operation(summary = "更新用户")
    @Log(module = SystemConstants.LogModule.SYSTEM, content = "更新用户")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SystemUser user) {
        user.setId(id);
        userService.updateUser(user);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户")
    @Log(module = SystemConstants.LogModule.SYSTEM, content = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 重置密码
     */
    @Operation(summary = "重置密码")
    @Log(module = SystemConstants.LogModule.SYSTEM, content = "重置用户密码")
    @PostMapping("/{id}/reset-password")
    public Result<Void> resetPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> params
    ) {
        String newPassword = params.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            newPassword = "123456";
        }
        userService.resetPassword(id, newPassword);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @Operation(summary = "修改密码")
    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        userService.changePassword(null, oldPassword, newPassword);
        return Result.success();
    }

    /**
     * 启用/禁用用户
     */
    @Operation(summary = "启用/禁用用户")
    @Log(module = SystemConstants.LogModule.SYSTEM, content = "修改用户状态")
    @PostMapping("/{id}/status")
    public Result<Void> changeStatus(
            @PathVariable Long id,
            @RequestParam Integer status
    ) {
        userService.changeStatus(id, status);
        return Result.success();
    }

    /**
     * 根据角色获取用户列表
     */
    @Operation(summary = "根据角色获取用户列表")
    @GetMapping("/by-role")
    public Result<List<SystemUser>> getByRole(@RequestParam String role) {
        return Result.success(userService.getUsersByRole(role));
    }

    /**
     * 获取所有客服
     */
    @Operation(summary = "获取所有客服")
    @GetMapping("/support")
    public Result<List<SystemUser>> getSupportUsers() {
        return Result.success(userService.getUsersByRole(SystemConstants.Role.SUPPORT));
    }

    /**
     * 获取所有客户成功
     */
    @Operation(summary = "获取所有客户成功")
    @GetMapping("/cs")
    public Result<List<SystemUser>> getCsUsers() {
        return Result.success(userService.getUsersByRole(SystemConstants.Role.CS));
    }
}
