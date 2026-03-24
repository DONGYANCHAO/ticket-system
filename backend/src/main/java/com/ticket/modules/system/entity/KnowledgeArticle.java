package com.ticket.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 知识库文章实体
 *
 * @author Ticket System
 */
@Data
@TableName("knowledge_article")
public class KnowledgeArticle implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 标签（JSON数组）
     */
    private String tags;

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 描述
     */
    private String description;

    /**
     * 可见性（ALL/INTERNAL/EXTERNAL）
     */
    private String visibility;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 有用次数
     */
    private Integer helpfulCount;

    /**
     * 状态（DRAFT/PUBLISHED/ARCHIVED）
     */
    private String status;

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
     * 删除标记
     */
    @TableLogic
    private Integer deleted;
}
