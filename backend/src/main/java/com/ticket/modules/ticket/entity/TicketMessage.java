package com.ticket.modules.ticket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工单消息实体
 *
 * @author Ticket System
 */
@Data
@TableName("ticket_message")
public class TicketMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 工单ID
     */
    private Long ticketId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型（PUBLIC/INTERNAL）
     */
    private String type;

    /**
     * 发送者类型（CUSTOMER/SUPPORT）
     */
    private String senderType;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 是否已读（0否 1是）
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 删除标记（0未删除 1已删除）
     */
    @TableLogic
    private Integer deleted;
}
