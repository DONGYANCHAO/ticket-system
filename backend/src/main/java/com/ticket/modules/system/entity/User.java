package com.ticket.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 *
 * @author Ticket System
 */
@Data
@TableName("system_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 头像URL */
    private String avatar;

    /** 角色: ADMIN/SUPPORT/CS/CUSTOMER */
    private String role;

    /** 所属客户ID(客户用户) */
    private Long customerId;

    /** 状态: 0禁用, 1启用 */
    private Integer status;

    /** 连续登录失败次数 */
    private Integer loginAttempts;

    /** 锁定截止时间 */
    private LocalDateTime lockedUntil;

    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;

    /** 最后登录IP */
    private String lastLoginIp;

    /** 创建人 */
    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 更新人 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /** 删除标记: 0未删除, 1已删除 */
    @TableLogic
    private Integer deleted;

    /**
     * 判断账号是否被锁定
     */
    public boolean isLocked() {
        return lockedUntil != null && LocalDateTime.now().isBefore(lockedUntil);
    }

    /**
     * 判断账号是否启用
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }

    /**
     * 判断是否为管理员
     */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    /**
     * 判断是否为技术支持
     */
    public boolean isSupport() {
        return "SUPPORT".equals(role);
    }

    /**
     * 判断是否为客户成功
     */
    public boolean isCs() {
        return "CS".equals(role);
    }

    /**
     * 判断是否为客户用户
     */
    public boolean isCustomer() {
        return "CUSTOMER".equals(role);
    }
}
