package com.ticket.modules.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户实体
 *
 * @author Ticket System
 */
@Data
@TableName("customer")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户名称
     */
    private String name;

    /**
     * 客户等级（STANDARD/ADVANCED/PREMIUM）
     */
    private String level;

    /**
     * 所属行业
     */
    private String industry;

    /**
     * 企业规模（SMALL/MEDIUM/LARGE）
     */
    private String scale;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人手机
     */
    private String contactPhone;

    /**
     * 联系人邮箱
     */
    private String contactEmail;

    /**
     * 客户地址
     */
    private String address;

    /**
     * 客户网站
     */
    private String website;

    /**
     * SLA ID
     */
    private Long slaId;

    /**
     * 服务到期日期
     */
    private LocalDateTime serviceExpireDate;

    /**
     * 年合同金额
     */
    private BigDecimal contractAmount;

    /**
     * 备注
     */
    private String remark;

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
