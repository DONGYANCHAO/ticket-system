package com.ticket.modules.ticket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工单实体
 *
 * @author Ticket System
 */
@Data
@TableName("ticket")
public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 工单编号
     */
    private String ticketNo;

    /**
     * 工单标题
     */
    private String title;

    /**
     * 工单类型（BUG/CONSULT/DEMAND/COMPLAINT）
     */
    private String type;

    /**
     * 优先级（URGENT/HIGH/MEDIUM/LOW）
     */
    private String priority;

    /**
     * 工单状态（NEW/CONFIRMED/PROCESSING/PENDING_VERIFY/SOLVED/CLOSED/WITHDRAWN/MERGED）
     */
    private String status;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 处理人ID
     */
    private Long handlerId;

    /**
     * 问题模块ID
     */
    private Long moduleId;

    /**
     * SLA ID
     */
    private Long slaId;

    /**
     * 问题描述
     */
    private String description;

    /**
     * 是否私有（0否 1是）
     */
    private Integer isPrivate;

    /**
     * 问题解决时间
     */
    private LocalDateTime solveTime;

    /**
     * 首次响应时间
     */
    private LocalDateTime firstResponseTime;

    /**
     * SLA首次响应时限（分钟）
     */
    private Integer slaFirstResponseMinutes;

    /**
     * SLA解决时限（分钟）
     */
    private Integer slaResolveMinutes;

    /**
     * 是否SLA预警
     */
    private Integer slaWarning;

    /**
     * 解决备注
     */
    private String solveRemark;

    /**
     * 关闭时间
     */
    private LocalDateTime closeTime;

    /**
     * 关闭原因
     */
    private String closeReason;

    /**
     * 解决评分
     */
    private Integer satisfactionScore;

    /**
     * 满意度评价
     */
    private String satisfactionRating;

    /**
     * 满意度反馈
     */
    private String satisfactionFeedback;

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
     * 创建人
     */
    private Long createBy;

    /**
     * 更新人
     */
    private Long updateBy;

    /**
     * 删除标记（0未删除 1已删除）
     */
    @TableLogic
    private Integer deleted;
}
