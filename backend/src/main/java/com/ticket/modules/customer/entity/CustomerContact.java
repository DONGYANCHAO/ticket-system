package com.ticket.modules.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客户联系人实体
 *
 * @author Ticket System
 */
@Data
@TableName("customer_contact")
public class CustomerContact implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 联系人ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 部门
     */
    private String department;

    /**
     * 职位
     */
    private String position;

    /**
     * 手机
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 是否为主要联系人（0否 1是）
     */
    private Integer isPrimary;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标记（0未删除 1已删除）
     */
    @TableLogic
    private Integer deleted;
}
