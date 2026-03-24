-- =============================================
-- 客户工单平台 - 数据库初始化脚本
-- 版本: 1.0.0
-- 日期: 2026-03-24
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS ticket_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ticket_system;

-- =============================================
-- 1. 客户表
-- =============================================
CREATE TABLE IF NOT EXISTS customer (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name            VARCHAR(100) NOT NULL COMMENT '企业名称',
    contact_name    VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    contact_phone   VARCHAR(20) NOT NULL COMMENT '联系人手机',
    contact_email   VARCHAR(100) NOT NULL COMMENT '联系人邮箱',
    enterprise_scale VARCHAR(20) DEFAULT NULL COMMENT '企业规模: SMALL/MEDIUM/LARGE',
    service_level   VARCHAR(20) DEFAULT 'STANDARD' COMMENT '服务等级: STANDARD/ADVANCED/PREMIUM',
    status          TINYINT DEFAULT 1 COMMENT '状态: 0禁用, 1启用',
    created_by      BIGINT UNSIGNED DEFAULT 0 COMMENT '创建人',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by      BIGINT UNSIGNED DEFAULT 0 COMMENT '更新人',
    updated_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT DEFAULT 0 COMMENT '删除标记: 0未删除, 1已删除',

    UNIQUE KEY uk_name (name),
    KEY idx_status (status),
    KEY idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户表';

-- =============================================
-- 2. 系统用户表
-- =============================================
CREATE TABLE IF NOT EXISTS system_user (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username        VARCHAR(50) NOT NULL COMMENT '用户名',
    password        VARCHAR(255) NOT NULL COMMENT '密码(加密存储)',
    real_name       VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    phone           VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    email           VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar          VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    role            VARCHAR(20) NOT NULL COMMENT '角色: ADMIN/SUPPORT/CS/CUSTOMER',
    customer_id     BIGINT UNSIGNED DEFAULT NULL COMMENT '所属客户ID(客户用户)',
    status          TINYINT DEFAULT 1 COMMENT '状态: 0禁用, 1启用',
    login_attempts  INT DEFAULT 0 COMMENT '连续登录失败次数',
    locked_until    DATETIME DEFAULT NULL COMMENT '锁定截止时间',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    last_login_ip   VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    created_by      BIGINT UNSIGNED DEFAULT 0 COMMENT '创建人',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by      BIGINT UNSIGNED DEFAULT 0 COMMENT '更新人',
    updated_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT DEFAULT 0 COMMENT '删除标记',

    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email),
    UNIQUE KEY uk_phone (phone),
    KEY idx_role (role),
    KEY idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- =============================================
-- 3. 问题模块表
-- =============================================
CREATE TABLE IF NOT EXISTS ticket_module (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    code            VARCHAR(50) NOT NULL COMMENT '模块代码',
    name            VARCHAR(100) NOT NULL COMMENT '模块名称',
    description     VARCHAR(500) DEFAULT NULL COMMENT '模块描述',
    sort_order      INT DEFAULT 0 COMMENT '排序',
    status          TINYINT DEFAULT 1 COMMENT '状态: 0禁用, 1启用',
    created_by      BIGINT UNSIGNED DEFAULT 0 COMMENT '创建人',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by      BIGINT UNSIGNED DEFAULT 0 COMMENT '更新人',
    updated_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问题模块表';

-- =============================================
-- 4. 工单表
-- =============================================
CREATE TABLE IF NOT EXISTS ticket (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    ticket_no       VARCHAR(30) NOT NULL COMMENT '工单编号: TK-YYYYMMDD-NNN',
    title           VARCHAR(200) NOT NULL COMMENT '工单标题',
    module          VARCHAR(50) NOT NULL COMMENT '问题模块',
    type            VARCHAR(20) NOT NULL COMMENT '问题类型: BUG/CONSULT/DEMAND/COMPLAINT',
    priority        VARCHAR(20) NOT NULL COMMENT '优先级: URGENT/HIGH/MEDIUM/LOW',
    description     TEXT COMMENT '问题描述(富文本HTML)',
    customer_id     BIGINT UNSIGNED NOT NULL COMMENT '所属客户ID',
    creator_id      BIGINT UNSIGNED NOT NULL COMMENT '创建人ID',
    assignee_id     BIGINT UNSIGNED DEFAULT NULL COMMENT '负责人ID',
    status          VARCHAR(20) NOT NULL DEFAULT 'NEW' COMMENT '状态: NEW/CONFIRMED/PROCESSING/PENDING_VERIFY/SOLVED/CLOSED/WITHDRAWN/MERGED',
    assignee_time   DATETIME DEFAULT NULL COMMENT '分配时间',
    first_response_time DATETIME DEFAULT NULL COMMENT '首次响应时间',
    resolved_time   DATETIME DEFAULT NULL COMMENT '解决时间',
    closed_time     DATETIME DEFAULT NULL COMMENT '关闭时间',
    sla_deadline    DATETIME DEFAULT NULL COMMENT 'SLA截止时间',
    response_sla_met TINYINT DEFAULT 1 COMMENT '首次响应SLA是否达标: 0否, 1是',
    resolve_sla_met TINYINT DEFAULT 1 COMMENT '解决SLA是否达标: 0否, 1是',
    is_overdue      TINYINT DEFAULT 0 COMMENT '是否已逾期: 0否, 1是',
    parent_ticket_id BIGINT UNSIGNED DEFAULT NULL COMMENT '合并主工单ID',
    merge_reason    TEXT DEFAULT NULL COMMENT '合并原因',
    created_by      BIGINT UNSIGNED DEFAULT 0 COMMENT '创建人',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by      BIGINT UNSIGNED DEFAULT 0 COMMENT '更新人',
    updated_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT DEFAULT 0 COMMENT '删除标记',

    UNIQUE KEY uk_ticket_no (ticket_no),
    KEY idx_module (module),
    KEY idx_type (type),
    KEY idx_priority (priority),
    KEY idx_customer_id (customer_id),
    KEY idx_creator_id (creator_id),
    KEY idx_assignee_id (assignee_id),
    KEY idx_status (status),
    KEY idx_parent_ticket_id (parent_ticket_id),
    KEY idx_created_time (created_time),
    KEY idx_sla_deadline (sla_deadline)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单表';

-- =============================================
-- 5. 工单附件表
-- =============================================
CREATE TABLE IF NOT EXISTS ticket_attachment (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    ticket_id       BIGINT UNSIGNED NOT NULL COMMENT '工单ID',
    file_name       VARCHAR(255) NOT NULL COMMENT '文件名',
    file_path       VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    file_size       BIGINT DEFAULT 0 COMMENT '文件大小(字节)',
    file_type       VARCHAR(50) DEFAULT NULL COMMENT '文件类型/MIME',
    is_image        TINYINT DEFAULT 0 COMMENT '是否图片: 0否, 1是',
    uploaded_by     BIGINT UNSIGNED NOT NULL COMMENT '上传人',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',

    KEY idx_ticket_id (ticket_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单附件表';

-- =============================================
-- 6. 工单标签表
-- =============================================
CREATE TABLE IF NOT EXISTS ticket_tag (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name            VARCHAR(50) NOT NULL COMMENT '标签名称',
    color           VARCHAR(20) DEFAULT '#409EFF' COMMENT '标签颜色',
    scope           VARCHAR(20) DEFAULT 'GLOBAL' COMMENT '可见范围: GLOBAL/MODULE',
    module          VARCHAR(50) DEFAULT NULL COMMENT '所属模块',
    status          TINYINT DEFAULT 1 COMMENT '状态: 0禁用, 1启用',
    sort_order      INT DEFAULT 0 COMMENT '排序',
    created_by      BIGINT UNSIGNED DEFAULT 0 COMMENT '创建人',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    UNIQUE KEY uk_name (name),
    KEY idx_scope (scope),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单标签表';

-- =============================================
-- 7. 工单-标签关联表
-- =============================================
CREATE TABLE IF NOT EXISTS ticket_tag_relation (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    ticket_id       BIGINT UNSIGNED NOT NULL COMMENT '工单ID',
    tag_id          BIGINT UNSIGNED NOT NULL COMMENT '标签ID',
    created_by      BIGINT UNSIGNED DEFAULT 0 COMMENT '操作人',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',

    UNIQUE KEY uk_ticket_tag (ticket_id, tag_id),
    KEY idx_ticket_id (ticket_id),
    KEY idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单标签关联表';

-- =============================================
-- 8. 工单关联表
-- =============================================
CREATE TABLE IF NOT EXISTS ticket_relation (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    ticket_id       BIGINT UNSIGNED NOT NULL COMMENT '工单ID',
    related_ticket_id BIGINT UNSIGNED NOT NULL COMMENT '关联工单ID',
    relation_type   VARCHAR(20) NOT NULL COMMENT '关联类型: SAME_ISSUE/PARENT_CHILD/REFERENCE',
    created_by      BIGINT UNSIGNED DEFAULT 0 COMMENT '操作人',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    KEY idx_ticket_id (ticket_id),
    KEY idx_related_ticket_id (related_ticket_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单关联表';

-- =============================================
-- 9. 工单抄送表
-- =============================================
CREATE TABLE IF NOT EXISTS ticket_cc (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    ticket_id       BIGINT UNSIGNED NOT NULL COMMENT '工单ID',
    user_id         BIGINT UNSIGNED NOT NULL COMMENT '抄送人ID',
    created_by      BIGINT UNSIGNED DEFAULT 0 COMMENT '操作人',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '抄送时间',

    UNIQUE KEY uk_ticket_user (ticket_id, user_id),
    KEY idx_ticket_id (ticket_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单抄送表';

-- =============================================
-- 10. 工单消息表
-- =============================================
CREATE TABLE IF NOT EXISTS ticket_message (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    ticket_id       BIGINT UNSIGNED NOT NULL COMMENT '工单ID',
    sender_id       BIGINT UNSIGNED NOT NULL COMMENT '发送人ID',
    sender_type     VARCHAR(20) NOT NULL COMMENT '发送人类型: CUSTOMER/SUPPORT',
    content         TEXT COMMENT '消息内容(富文本HTML)',
    message_type    VARCHAR(20) NOT NULL DEFAULT 'PUBLIC' COMMENT '消息类型: PUBLIC/INTERNAL',
    reply_to_id     BIGINT UNSIGNED DEFAULT NULL COMMENT '回复的消息ID',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    updated_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_edited       TINYINT DEFAULT 0 COMMENT '是否已编辑: 0否, 1是',
    is_recalled     TINYINT DEFAULT 0 COMMENT '是否已撤回: 0否, 1是',

    KEY idx_ticket_id (ticket_id),
    KEY idx_sender_id (sender_id),
    KEY idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单消息表';

-- =============================================
-- 11. 消息附件表
-- =============================================
CREATE TABLE IF NOT EXISTS message_attachment (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    message_id      BIGINT UNSIGNED NOT NULL COMMENT '消息ID',
    file_name       VARCHAR(255) NOT NULL COMMENT '文件名',
    file_path       VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    file_size       BIGINT DEFAULT 0 COMMENT '文件大小(字节)',
    file_type       VARCHAR(50) DEFAULT NULL COMMENT '文件类型',
    is_image        TINYINT DEFAULT 0 COMMENT '是否图片',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',

    KEY idx_message_id (message_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息附件表';

-- =============================================
-- 12. 工单变更日志表
-- =============================================
CREATE TABLE IF NOT EXISTS ticket_history (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    ticket_id       BIGINT UNSIGNED NOT NULL COMMENT '工单ID',
    operate_type    VARCHAR(50) NOT NULL COMMENT '操作类型',
    operate_content TEXT COMMENT '变更内容(JSON格式)',
    operator_id     BIGINT UNSIGNED NOT NULL COMMENT '操作人ID',
    operator_name   VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
    operate_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',

    KEY idx_ticket_id (ticket_id),
    KEY idx_operate_type (operate_type),
    KEY idx_operate_time (operate_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单变更日志表';

-- =============================================
-- 13. 满意度评价表
-- =============================================
CREATE TABLE IF NOT EXISTS satisfaction_feedback (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    ticket_id       BIGINT UNSIGNED NOT NULL COMMENT '工单ID',
    customer_id     BIGINT UNSIGNED NOT NULL COMMENT '客户ID',
    rating          TINYINT NOT NULL COMMENT '满意度评分: 1-5',
    response_rating VARCHAR(20) DEFAULT NULL COMMENT '响应速度: SATISFIED/AVERAGE/DISSATISFIED',
    quality_rating  VARCHAR(20) DEFAULT NULL COMMENT '处理质量: SATISFIED/AVERAGE/DISSATISFIED',
    content         TEXT DEFAULT NULL COMMENT '评价内容',
    is_anonymous    TINYINT DEFAULT 0 COMMENT '是否匿名: 0否, 1是',
    feedback_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    handler_notified TINYINT DEFAULT 0 COMMENT '是否已通知处理人: 0否, 1是',
    notify_time     DATETIME DEFAULT NULL COMMENT '通知时间',

    UNIQUE KEY uk_ticket_id (ticket_id),
    KEY idx_rating (rating),
    KEY idx_feedback_time (feedback_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='满意度评价表';

-- =============================================
-- 14. 催单记录表
-- =============================================
CREATE TABLE IF NOT EXISTS ticket_reminder (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    ticket_id       BIGINT UNSIGNED NOT NULL COMMENT '工单ID',
    reminder_count  INT DEFAULT 1 COMMENT '催单次数',
    remind_by       BIGINT UNSIGNED NOT NULL COMMENT '催单人',
    remind_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '催单时间',
    reason          TEXT DEFAULT NULL COMMENT '催单原因',

    KEY idx_ticket_id (ticket_id),
    KEY idx_remind_time (remind_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='催单记录表';

-- =============================================
-- 15. 知识库文章表
-- =============================================
CREATE TABLE IF NOT EXISTS knowledge_article (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title           VARCHAR(200) NOT NULL COMMENT '文章标题',
    content         TEXT COMMENT '文章内容(富文本HTML)',
    module          VARCHAR(50) DEFAULT NULL COMMENT '关联模块',
    category        VARCHAR(50) DEFAULT NULL COMMENT '分类',
    tags            VARCHAR(500) DEFAULT NULL COMMENT '标签,逗号分隔',
    visibility      VARCHAR(20) DEFAULT 'ALL' COMMENT '可见性: ALL/INTERNAL/EXTERNAL',
    status          VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态: DRAFT/PUBLISHED/ARCHIVED',
    view_count      INT DEFAULT 0 COMMENT '浏览次数',
    helpful_count   INT DEFAULT 0 COMMENT '有帮助次数',
    not_helpful_count INT DEFAULT 0 COMMENT '没帮助次数',
    author_id       BIGINT UNSIGNED DEFAULT NULL COMMENT '作者ID',
    published_time  DATETIME DEFAULT NULL COMMENT '发布时间',
    created_by      BIGINT UNSIGNED DEFAULT 0 COMMENT '创建人',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by      BIGINT UNSIGNED DEFAULT 0 COMMENT '更新人',
    updated_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT DEFAULT 0 COMMENT '删除标记',

    KEY idx_module (module),
    KEY idx_category (category),
    KEY idx_status (status),
    KEY idx_visibility (visibility),
    KEY idx_published_time (published_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库文章表';

-- =============================================
-- 16. 文章版本表
-- =============================================
CREATE TABLE IF NOT EXISTS article_version (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    article_id      BIGINT UNSIGNED NOT NULL COMMENT '文章ID',
    version         INT NOT NULL COMMENT '版本号',
    title           VARCHAR(200) NOT NULL COMMENT '文章标题',
    content         TEXT COMMENT '文章内容',
    change_summary  TEXT DEFAULT NULL COMMENT '变更说明',
    editor_id       BIGINT UNSIGNED NOT NULL COMMENT '编辑人',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    KEY idx_article_id (article_id),
    KEY idx_version (version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章版本表';

-- =============================================
-- 17. 快捷回复模板表
-- =============================================
CREATE TABLE IF NOT EXISTS quick_reply_template (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name            VARCHAR(100) NOT NULL COMMENT '模板名称',
    content         TEXT NOT NULL COMMENT '模板内容',
    module          VARCHAR(50) DEFAULT NULL COMMENT '适用模块',
    ticket_type     VARCHAR(20) DEFAULT NULL COMMENT '适用问题类型',
    variables       VARCHAR(500) DEFAULT NULL COMMENT '可用变量',
    use_count       INT DEFAULT 0 COMMENT '使用次数',
    status          TINYINT DEFAULT 1 COMMENT '状态: 0禁用, 1启用',
    created_by      BIGINT UNSIGNED DEFAULT 0 COMMENT '创建人',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    KEY idx_module (module),
    KEY idx_ticket_type (ticket_type),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='快捷回复模板表';

-- =============================================
-- 18. 通知配置表
-- =============================================
CREATE TABLE IF NOT EXISTS notification_config (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id         BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    channel         VARCHAR(20) NOT NULL COMMENT '通知渠道: EMAIL/DINGTALK/SMS',
    enabled         TINYINT DEFAULT 1 COMMENT '是否启用: 0否, 1是',
    event_types     VARCHAR(500) DEFAULT NULL COMMENT '订阅的通知类型',
    quiet_hours_start TIME DEFAULT NULL COMMENT '免打扰开始时间',
    quiet_hours_end   TIME DEFAULT NULL COMMENT '免打扰结束时间',
    updated_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    KEY idx_user_id (user_id),
    KEY idx_channel (channel)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知配置表';

-- =============================================
-- 19. 通知记录表
-- =============================================
CREATE TABLE IF NOT EXISTS notification_record (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id         BIGINT UNSIGNED NOT NULL COMMENT '通知对象ID',
    event_type      VARCHAR(50) NOT NULL COMMENT '事件类型',
    title           VARCHAR(200) DEFAULT NULL COMMENT '通知标题',
    content         TEXT COMMENT '通知内容',
    channel         VARCHAR(20) NOT NULL COMMENT '通知渠道',
    status          VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING/SENT/FAILED',
    sent_time       DATETIME DEFAULT NULL COMMENT '发送时间',
    error_message   VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    KEY idx_user_id (user_id),
    KEY idx_event_type (event_type),
    KEY idx_status (status),
    KEY idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知记录表';

-- =============================================
-- 20. 操作日志表
-- =============================================
CREATE TABLE IF NOT EXISTS operation_log (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id         BIGINT UNSIGNED NOT NULL COMMENT '操作用户ID',
    username        VARCHAR(50) DEFAULT NULL COMMENT '用户名',
    module          VARCHAR(50) NOT NULL COMMENT '操作模块',
    operation       VARCHAR(100) NOT NULL COMMENT '操作类型',
    request_method  VARCHAR(10) DEFAULT NULL COMMENT '请求方法',
    request_url     VARCHAR(500) DEFAULT NULL COMMENT '请求URL',
    request_params  TEXT DEFAULT NULL COMMENT '请求参数',
    response_code   VARCHAR(20) DEFAULT NULL COMMENT '响应码',
    client_ip       VARCHAR(50) DEFAULT NULL COMMENT '客户端IP',
    user_agent      VARCHAR(500) DEFAULT NULL COMMENT 'User-Agent',
    operate_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    execution_time  BIGINT DEFAULT 0 COMMENT '执行时长(毫秒)',

    KEY idx_user_id (user_id),
    KEY idx_module (module),
    KEY idx_operate_time (operate_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- =============================================
-- 21. 草稿表
-- =============================================
CREATE TABLE IF NOT EXISTS draft (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id         BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    draft_type      VARCHAR(30) NOT NULL COMMENT '草稿类型: TICKET/COMMENT',
    business_id     BIGINT UNSIGNED DEFAULT NULL COMMENT '关联业务ID(编辑时)',
    content         TEXT COMMENT '草稿内容(JSON格式)',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    KEY idx_user_draft (user_id, draft_type, business_id),
    KEY idx_updated_time (updated_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='草稿表';

-- =============================================
-- 22. 报表模板表
-- =============================================
CREATE TABLE IF NOT EXISTS report_template (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name            VARCHAR(100) NOT NULL COMMENT '模板名称',
    description     VARCHAR(500) DEFAULT NULL COMMENT '模板描述',
    data_scope      VARCHAR(50) DEFAULT 'ALL' COMMENT '数据范围: ALL/CUSTOMER/MODULE',
    default_filters TEXT DEFAULT NULL COMMENT '默认筛选条件(JSON)',
    chart_configs    TEXT DEFAULT NULL COMMENT '图表配置(JSON)',
    column_config   TEXT DEFAULT NULL COMMENT '列配置(JSON)',
    group_by        VARCHAR(100) DEFAULT NULL COMMENT '分组维度',
    metrics         VARCHAR(500) DEFAULT NULL COMMENT '计算指标',
    is_public       TINYINT DEFAULT 0 COMMENT '是否公开: 0否, 1是',
    creator_id      BIGINT UNSIGNED NOT NULL COMMENT '创建人',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    KEY idx_creator_id (creator_id),
    KEY idx_is_public (is_public)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报表模板表';

-- =============================================
-- 23. 报表订阅表
-- =============================================
CREATE TABLE IF NOT EXISTS report_subscription (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    template_id     BIGINT UNSIGNED NOT NULL COMMENT '模板ID',
    user_id         BIGINT UNSIGNED NOT NULL COMMENT '订阅用户',
    schedule_type    VARCHAR(20) NOT NULL COMMENT '调度类型: DAILY/WEEKLY/MONTHLY',
    schedule_cron   VARCHAR(50) DEFAULT NULL COMMENT 'Cron表达式',
    recipients      VARCHAR(500) NOT NULL COMMENT '接收人邮箱',
    status          TINYINT DEFAULT 1 COMMENT '状态: 0禁用, 1启用',
    last_run_time   DATETIME DEFAULT NULL COMMENT '上次运行时间',
    last_status     VARCHAR(20) DEFAULT NULL COMMENT '上次运行状态',
    created_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    KEY idx_template_id (template_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报表订阅表';

-- =============================================
-- 初始化数据
-- =============================================

-- 插入问题模块初始数据
INSERT INTO ticket_module (code, name, description, sort_order) VALUES
('offline_develop', '离线开发', '离线数据开发相关问题', 1),
('realtime_develop', '实时开发', '实时数据处理相关问题', 2),
('data_service', '数据服务', '数据服务调用相关问题', 3),
('data_asset', '数据资产', '数据资产管理相关问题', 4),
('smart_metric', '智能指标', '智能指标配置相关问题', 5),
('smart_tag', '智能标签', '智能标签应用相关问题', 6),
('easymanager', 'Easymanager', '系统管理相关问题', 7),
('hadoop', 'Hadoop', 'Hadoop生态相关问题', 8);

-- 插入默认标签
INSERT INTO ticket_tag (name, color, scope) VALUES
('紧急', '#F56C6C', 'GLOBAL'),
('已复现', '#67C23A', 'GLOBAL'),
('需复现', '#E6A23C', 'GLOBAL'),
('待排期', '#909399', 'GLOBAL'),
('已升级', '#9C27B0', 'GLOBAL');

COMMIT;
