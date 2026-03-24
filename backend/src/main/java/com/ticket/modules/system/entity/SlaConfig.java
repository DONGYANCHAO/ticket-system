package com.ticket.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * SLA配置实体
 *
 * @author Ticket System
 */
@Data
@TableName("sla_config")
public class SlaConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * SLA ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * SLA名称
     */
    private String name;

    /**
     * 服务等级（STANDARD/ADVANCED/PREMIUM）
     */
    private String level;

    /**
     * 首次响应时限（分钟）
     */
    private Integer firstResponseMinutes;

    /**
     * 问题解决时限（分钟）
     */
    private Integer resolveMinutes;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态（0禁用 1启用）
     */
    private Integer status;

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
     * 删除标记
     */
    @TableLogic
    private Integer deleted;
}
