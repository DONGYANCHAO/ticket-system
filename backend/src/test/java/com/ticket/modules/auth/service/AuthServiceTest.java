package com.ticket.modules.auth.service;

import com.ticket.modules.auth.dto.LoginRequest;
import com.ticket.modules.auth.dto.RegisterRequest;
import com.ticket.modules.system.entity.User;
import com.ticket.modules.system.mapper.UserMapper;
import com.ticket.config.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 认证服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("认证服务测试")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPhone("13800138000");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setEmail("test@example.com");
        testUser.setStatus(1);
    }

    @Test
    @DisplayName("用户登录 - 成功")
    void login_Success() {
        // given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(jwtUtils.generateToken(any(User.class))).thenReturn("test.jwt.token");
        when(jwtUtils.generateRefreshToken(any(User.class))).thenReturn("refresh.token");

        // when
        Map<String, Object> result = authService.login(loginRequest);

        // then
        assertNotNull(result);
        assertEquals("test.jwt.token", result.get("token"));
        assertEquals("refresh.token", result.get("refreshToken"));
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    @DisplayName("用户注册 - 成功")
    void register_Success() {
        // given
        when(userMapper.selectByUsername("newuser")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // when
        boolean result = authService.register(registerRequest);

        // then
        assertTrue(result);
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    @DisplayName("用户注册 - 用户名已存在")
    void register_UsernameExists() {
        // given
        when(userMapper.selectByUsername("newuser")).thenReturn(testUser);

        // when & then
        assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });
    }

    @Test
    @DisplayName("刷新 Token - 成功")
    void refreshToken_Success() {
        // given
        String refreshToken = "valid.refresh.token";
        when(jwtUtils.validateToken(refreshToken)).thenReturn(true);
        when(jwtUtils.getUsernameFromToken(refreshToken)).thenReturn("testuser");
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);
        when(jwtUtils.generateToken(testUser)).thenReturn("new.jwt.token");
        when(jwtUtils.generateRefreshToken(testUser)).thenReturn("new.refresh.token");

        // when
        Map<String, Object> result = authService.refreshToken(refreshToken);

        // then
        assertNotNull(result);
        assertEquals("new.jwt.token", result.get("token"));
    }

    @Test
    @DisplayName("刷新 Token - 无效 Token")
    void refreshToken_InvalidToken() {
        // given
        String invalidToken = "invalid.token";
        when(jwtUtils.validateToken(invalidToken)).thenReturn(false);

        // when & then
        assertThrows(RuntimeException.class, () -> {
            authService.refreshToken(invalidToken);
        });
    }

    @Test
    @DisplayName("登出 - 成功")
    void logout_Success() {
        // given
        String token = "valid.token";
        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.getUsernameFromToken(token)).thenReturn("testuser");

        // when
        boolean result = authService.logout(token);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("获取当前用户信息 - 成功")
    void getCurrentUser_Success() {
        // given
        String token = "valid.token";
        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.getUsernameFromToken(token)).thenReturn("testuser");
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        // when
        User result = authService.getCurrentUser(token);

        // then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }
}
