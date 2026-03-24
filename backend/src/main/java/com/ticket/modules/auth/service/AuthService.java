package com.ticket.modules.auth.service;

import com.ticket.modules.auth.dto.LoginRequest;
import com.ticket.modules.auth.dto.RegisterRequest;

import java.util.Map;

/**
 * 认证服务接口
 *
 * @author Ticket System
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录结果（包含Token和用户信息）
     */
    Map<String, Object> login(LoginRequest request);

    /**
     * 用户注册
     *
     * @param request 注册请求
     */
    void register(RegisterRequest request);

    /**
     * 刷新Token
     *
     * @param token 旧Token
     * @return 新Token信息
     */
    Map<String, Object> refreshToken(String token);

    /**
     * 退出登录
     */
    void logout();

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    Map<String, Object> getCurrentUserInfo();

    /**
     * 发送验证码
     *
     * @param email 邮箱
     */
    void sendVerifyCode(String email);

    /**
     * 重置密码
     *
     * @param email        邮箱
     * @param code         验证码
     * @param newPassword  新密码
     */
    void resetPassword(String email, String code, String newPassword);

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(String oldPassword, String newPassword);
}
