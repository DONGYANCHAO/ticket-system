package com.ticket.modules.ticket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工单变更日志实体
 *
 * @author Ticket System
 */
@Data
@TableName("ticket_history")
public class TicketHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 工单ID
     */
    private Long ticketId;

    /**
     * 操作类型
     */
    private String action;

    /**
     * 操作内容
     */
    private String content;

    /**
     * 操作前值
     */
    private String oldValue;

    /**
     * 操作后值
     */
    private String newValue;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 操作人类型（CUSTOMER/SUPPORT/SYSTEM）
     */
    private String operatorType;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
