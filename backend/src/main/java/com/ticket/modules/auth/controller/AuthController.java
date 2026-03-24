package com.ticket.modules.auth.controller;

import com.ticket.common.result.Result;
import com.ticket.modules.auth.dto.LoginRequest;
import com.ticket.modules.auth.dto.RegisterRequest;
import com.ticket.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 *
 * @author Ticket System
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.success();
    }

    /**
     * 刷新Token
     */
    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<Map<String, Object>> refresh(@RequestHeader("Authorization") String token) {
        return Result.success(authService.refreshToken(token));
    }

    /**
     * 退出登录
     */
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/current")
    public Result<Map<String, Object>> getCurrentUser() {
        return Result.success(authService.getCurrentUserInfo());
    }

    /**
     * 发送验证码
     */
    @Operation(summary = "发送验证码")
    @PostMapping("/send-code")
    public Result<Void> sendCode(@RequestParam String email) {
        authService.sendVerifyCode(email);
        return Result.success();
    }

    /**
     * 重置密码
     */
    @Operation(summary = "重置密码")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestParam String email, @RequestParam String code, @RequestParam String newPassword) {
        authService.resetPassword(email, code, newPassword);
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
        authService.changePassword(oldPassword, newPassword);
        return Result.success();
    }
}
