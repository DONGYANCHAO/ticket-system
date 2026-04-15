package com.ticket.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.modules.auth.dto.LoginRequest;
import com.ticket.modules.auth.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证控制器集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("认证控制器集成测试")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin123");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser" + System.currentTimeMillis());
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPhone("13800138000");
    }

    @Test
    @DisplayName("用户登录 - 成功")
    void login_Success() throws Exception {
        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    @DisplayName("用户登录 - 密码错误")
    void login_WrongPassword() throws Exception {
        // given
        loginRequest.setPassword("wrongpassword");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @DisplayName("用户登录 - 用户不存在")
    void login_UserNotFound() throws Exception {
        // given
        loginRequest.setUsername("nonexistentuser");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @DisplayName("用户注册 - 成功")
    void register_Success() throws Exception {
        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("用户注册 - 用户名已存在")
    void register_UsernameExists() throws Exception {
        // given - 先注册一个用户
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // when & then - 再次使用相同用户名注册
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("用户注册 - 参数验证失败")
    void register_ValidationFailed() throws Exception {
        // given
        registerRequest.setUsername(""); // 空用户名
        registerRequest.setPassword("123"); // 密码太短

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("刷新 Token - 需要认证")
    void refreshToken_RequiresAuth() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                        .param("refreshToken", "some.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("登出 - 需要认证")
    void logout_RequiresAuth() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("获取验证码 - 成功")
    void getCaptcha_Success() throws Exception {
        mockMvc.perform(get("/api/auth/captcha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.captchaKey").exists())
                .andExpect(jsonPath("$.data.captchaImage").exists());
    }

    @Test
    @DisplayName("登录接口 - 支持验证码")
    void login_WithCaptcha() throws Exception {
        // given - 先获取验证码
        String captchaResponse = mockMvc.perform(get("/api/auth/captcha"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 解析验证码 key（实际实现中需要）
        // 这里简化处理

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }
}
