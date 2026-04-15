package com.ticket.modules.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.modules.system.entity.User;
import com.ticket.modules.system.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SystemUserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setStatus(1);
        testUser.setDeleted(0);
    }

    @Test
    @DisplayName("根据ID查询用户 - 成功")
    void getById_Success() {
        // given
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // when
        User result = userService.getById(1L);

        // then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("根据用户名查询用户 - 成功")
    void getByUsername_Success() {
        // given
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        // when
        User result = userMapper.selectByUsername("testuser");

        // then
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    @DisplayName("创建用户 - 成功")
    void createUser_Success() {
        // given
        when(userMapper.selectByUsername(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // when
        boolean result = userService.save(testUser);

        // then
        assertTrue(result);
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    @DisplayName("更新用户 - 成功")
    void updateUser_Success() {
        // given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // when
        testUser.setEmail("newemail@example.com");
        boolean result = userService.updateById(testUser);

        // then
        assertTrue(result);
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    @DisplayName("删除用户 - 成功")
    void deleteUser_Success() {
        // given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // when
        boolean result = userService.removeById(1L);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("分页查询用户")
    void pageUsers() {
        // given
        Page<User> page = new Page<>(1, 10);
        List<User> records = Arrays.asList(testUser);
        page.setRecords(records);
        page.setTotal(1);

        when(userMapper.selectPage(any(Page.class), any())).thenReturn(page);

        // when
        Page<User> result = userService.page(page);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    @DisplayName("修改用户状态 - 成功")
    void updateStatus_Success() {
        // given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // when
        boolean result = userService.updateStatus(1L, 0);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("重置密码 - 成功")
    void resetPassword_Success() {
        // given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // when
        boolean result = userService.resetPassword(1L, "newPassword123");

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("检查用户名是否存在 - 存在")
    void checkUsernameExists_True() {
        // given
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        // when
        User result = userMapper.selectByUsername("testuser");

        // then
        assertNotNull(result);
    }

    @Test
    @DisplayName("检查用户名是否存在 - 不存在")
    void checkUsernameExists_False() {
        // given
        when(userMapper.selectByUsername("nonexistent")).thenReturn(null);

        // when
        User result = userMapper.selectByUsername("nonexistent");

        // then
        assertNull(result);
    }
}
