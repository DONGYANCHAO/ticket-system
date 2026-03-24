package com.ticket.modules.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ticket.common.constant.SystemConstants;
import com.ticket.common.exception.BusinessException;
import com.ticket.common.util.SecurityUtils;
import com.ticket.config.JwtUtils;
import com.ticket.modules.auth.dto.LoginRequest;
import com.ticket.modules.auth.dto.RegisterRequest;
import com.ticket.modules.auth.service.AuthService;
import com.ticket.modules.system.entity.SystemUser;
import com.ticket.modules.system.mapper.SystemUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 *
 * @author Ticket System
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SystemUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.expiration:86400}")
    private long tokenExpiration;

    @Value("${jwt.refresh-expiration:604800}")
    private long refreshExpiration;

    @Override
    public Map<String, Object> login(LoginRequest request) {
        // 查询用户
        SystemUser user = userMapper.selectOne(
            new LambdaQueryWrapper<SystemUser>()
                .eq(SystemUser::getUsername, request.getUsername())
                .eq(SystemUser::getStatus, SystemConstants.Status.ENABLED)
        );

        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 更新最后登录时间
        userMapper.updateLastLoginTime(user.getId());

        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtUtils.generateRefreshToken(user.getId());

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("refreshToken", refreshToken);
        result.put("expiresIn", tokenExpiration);
        result.put("userInfo", buildUserInfo(user));

        log.info("用户登录成功: {}", user.getUsername());
        return result;
    }

    @Override
    public void register(RegisterRequest request) {
        // 检查用户名是否存在
        SystemUser existUser = userMapper.selectOne(
            new LambdaQueryWrapper<SystemUser>()
                .eq(SystemUser::getUsername, request.getUsername())
        );

        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否存在
        SystemUser existEmail = userMapper.selectOne(
            new LambdaQueryWrapper<SystemUser>()
                .eq(SystemUser::getEmail, request.getEmail())
        );

        if (existEmail != null) {
            throw new BusinessException("邮箱已被注册");
        }

        // 创建用户
        SystemUser user = new SystemUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(SystemConstants.Role.CUSTOMER);
        user.setStatus(SystemConstants.Status.ENABLED);

        userMapper.insert(user);
        log.info("用户注册成功: {}", user.getUsername());
    }

    @Override
    public Map<String, Object> refreshToken(String token) {
        String oldToken = token.replace("Bearer ", "");

        // 验证Token
        if (!jwtUtils.validateToken(oldToken)) {
            throw new BusinessException("Token无效");
        }

        // 获取用户ID
        Long userId = jwtUtils.getUserIdFromToken(oldToken);

        // 查询用户
        SystemUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 生成新Token
        String newToken = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        String newRefreshToken = jwtUtils.generateRefreshToken(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", newToken);
        result.put("refreshToken", newRefreshToken);
        result.put("expiresIn", tokenExpiration);

        return result;
    }

    @Override
    public void logout() {
        // 从Token中获取用户ID并加入黑名单
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId != null) {
            String blacklistKey = "token:blacklist:" + userId;
            redisTemplate.opsForValue().set(blacklistKey, "1", tokenExpiration, TimeUnit.SECONDS);
        }
        log.info("用户退出登录: {}", userId);
    }

    @Override
    public Map<String, Object> getCurrentUserInfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        SystemUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return buildUserInfo(user);
    }

    @Override
    public void sendVerifyCode(String email) {
        // 检查邮箱是否已注册
        SystemUser user = userMapper.selectOne(
            new LambdaQueryWrapper<SystemUser>()
                .eq(SystemUser::getEmail, email)
        );

        if (user == null) {
            throw new BusinessException("该邮箱未注册");
        }

        // 生成6位验证码
        String code = String.format("%06d", new Random().nextInt(1000000));

        // 存入Redis，5分钟有效
        String key = "verify:code:" + email;
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

        // TODO: 发送邮件
        log.info("发送验证码到邮箱: {}, 验证码: {}", email, code);
    }

    @Override
    public void resetPassword(String email, String code, String newPassword) {
        // 验证验证码
        String key = "verify:code:" + email;
        String cachedCode = (String) redisTemplate.opsForValue().get(key);

        if (cachedCode == null || !cachedCode.equals(code)) {
            throw new BusinessException("验证码错误或已过期");
        }

        // 查询用户
        SystemUser user = userMapper.selectOne(
            new LambdaQueryWrapper<SystemUser>()
                .eq(SystemUser::getEmail, email)
        );

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);

        // 删除验证码
        redisTemplate.delete(key);

        log.info("密码重置成功: {}", email);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Long userId = SecurityUtils.getCurrentUserId();
        SystemUser user = userMapper.selectById(userId);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);

        log.info("密码修改成功: {}", user.getUsername());
    }

    /**
     * 构建用户信息
     */
    private Map<String, Object> buildUserInfo(SystemUser user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("email", user.getEmail());
        userInfo.put("phone", user.getPhone());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("role", user.getRole());
        userInfo.put("department", user.getDepartment());
        userInfo.put("position", user.getPosition());
        return userInfo;
    }
}
