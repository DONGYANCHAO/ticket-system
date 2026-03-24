package com.ticket.modules.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ticket.modules.system.entity.SystemUser;

import java.util.List;

/**
 * 系统用户服务接口
 *
 * @author Ticket System
 */
public interface SystemUserService extends IService<SystemUser> {

    /**
     * 分页查询用户列表
     */
    Page<SystemUser> pageUser(Page<SystemUser> page, SystemUser query);

    /**
     * 根据用户名查询用户
     */
    SystemUser getByUsername(String username);

    /**
     * 新增用户
     */
    boolean addUser(SystemUser user);

    /**
     * 更新用户
     */
    boolean updateUser(SystemUser user);

    /**
     * 删除用户
     */
    boolean deleteUser(Long id);

    /**
     * 重置密码
     */
    boolean resetPassword(Long id, String newPassword);

    /**
     * 修改密码
     */
    boolean changePassword(Long id, String oldPassword, String newPassword);

    /**
     * 根据角色获取用户列表
     */
    List<SystemUser> getUsersByRole(String role);

    /**
     * 启用/禁用用户
     */
    boolean changeStatus(Long id, Integer status);
}
