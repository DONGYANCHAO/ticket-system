package com.ticket.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ticket.common.constant.SystemConstants;
import com.ticket.common.exception.BusinessException;
import com.ticket.common.util.SecurityUtils;
import com.ticket.modules.system.entity.SystemUser;
import com.ticket.modules.system.mapper.SystemUserMapper;
import com.ticket.modules.system.service.SystemUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 系统用户服务实现
 *
 * @author Ticket System
 */
@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<SystemUser> pageUser(Page<SystemUser> page, SystemUser query) {
        LambdaQueryWrapper<SystemUser> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getUsername())) {
            wrapper.like(SystemUser::getUsername, query.getUsername());
        }
        if (StringUtils.hasText(query.getRealName())) {
            wrapper.like(SystemUser::getRealName, query.getRealName());
        }
        if (StringUtils.hasText(query.getRole())) {
            wrapper.eq(SystemUser::getRole, query.getRole());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SystemUser::getStatus, query.getStatus());
        }

        wrapper.orderByDesc(SystemUser::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public SystemUser getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<SystemUser>()
                .eq(SystemUser::getUsername, username)
                .eq(SystemUser::getDeleted, 0));
    }

    @Override
    public boolean addUser(SystemUser user) {
        // 检查用户名是否存在
        if (getByUsername(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否存在
        if (StringUtils.hasText(user.getEmail())) {
            SystemUser existEmail = getOne(new LambdaQueryWrapper<SystemUser>()
                    .eq(SystemUser::getEmail, user.getEmail())
                    .eq(SystemUser::getDeleted, 0));
            if (existEmail != null) {
                throw new BusinessException("邮箱已被使用");
            }
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 设置默认值
        if (user.getStatus() == null) {
            user.setStatus(SystemConstants.Status.ENABLED);
        }

        return save(user);
    }

    @Override
    public boolean updateUser(SystemUser user) {
        SystemUser existUser = getById(user.getId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 如果修改了邮箱，检查邮箱唯一性
        if (StringUtils.hasText(user.getEmail()) && !user.getEmail().equals(existUser.getEmail())) {
            SystemUser existEmail = getOne(new LambdaQueryWrapper<SystemUser>()
                    .eq(SystemUser::getEmail, user.getEmail())
                    .eq(SystemUser::getDeleted, 0));
            if (existEmail != null) {
                throw new BusinessException("邮箱已被使用");
            }
        }

        // 不允许通过此方法修改密码
        user.setPassword(null);

        return updateById(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        // 不允许删除自己
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (id.equals(currentUserId)) {
            throw new BusinessException("不能删除当前登录用户");
        }

        return removeById(id);
    }

    @Override
    public boolean resetPassword(Long id, String newPassword) {
        SystemUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return updateById(user);
    }

    @Override
    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        SystemUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return updateById(user);
    }

    @Override
    public List<SystemUser> getUsersByRole(String role) {
        return list(new LambdaQueryWrapper<SystemUser>()
                .eq(SystemUser::getRole, role)
                .eq(SystemUser::getStatus, SystemConstants.Status.ENABLED)
                .eq(SystemUser::getDeleted, 0)
                .orderByAsc(SystemUser::getRealName));
    }

    @Override
    public boolean changeStatus(Long id, Integer status) {
        SystemUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setStatus(status);
        return updateById(user);
    }
}
