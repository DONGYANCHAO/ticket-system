-- ==========================================
-- H2 测试数据库表结构
-- ==========================================

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(255),
    status TINYINT DEFAULT 1 COMMENT '0-禁用, 1-启用',
    last_login_time TIMESTAMP,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 工单表
CREATE TABLE IF NOT EXISTS ticket (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    status TINYINT DEFAULT 0 COMMENT '0-待处理, 1-处理中, 2-待验证, 3-已解决, 4-已关闭',
    priority TINYINT DEFAULT 1 COMMENT '0-低, 1-中, 2-高, 3-紧急',
    type VARCHAR(50),
    source VARCHAR(50),
    creator_id BIGINT,
    handler_id BIGINT,
    customer_id BIGINT,
    sla_deadline TIMESTAMP,
    resolved_time TIMESTAMP,
    closed_time TIMESTAMP,
    satisfaction TINYINT,
    remark TEXT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 工单历史记录表
CREATE TABLE IF NOT EXISTS ticket_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_id BIGINT NOT NULL,
    operation VARCHAR(50) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    operator_id BIGINT,
    remark TEXT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 工单消息表
CREATE TABLE IF NOT EXISTS ticket_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    sender_id BIGINT,
    sender_type TINYINT DEFAULT 0 COMMENT '0-客户, 1-客服',
    message_type TINYINT DEFAULT 0 COMMENT '0-文本, 1-图片, 2-文件',
    attachments TEXT,
    is_internal TINYINT DEFAULT 0 COMMENT '0-外部, 1-内部',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 客户表
CREATE TABLE IF NOT EXISTS customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    company VARCHAR(200),
    address TEXT,
    level TINYINT DEFAULT 1 COMMENT '0-普通, 1-重要, 2-VIP',
    status TINYINT DEFAULT 1,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- SLA 配置表
CREATE TABLE IF NOT EXISTS sla_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    priority TINYINT NOT NULL,
    response_time INT COMMENT '响应时间(分钟)',
    resolve_time INT COMMENT '解决时间(分钟)',
    work_start_time TIME DEFAULT '09:00:00',
    work_end_time TIME DEFAULT '18:00:00',
    work_days VARCHAR(20) DEFAULT '1,2,3,4,5',
    status TINYINT DEFAULT 1,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_ticket_status ON ticket(status);
CREATE INDEX IF NOT EXISTS idx_ticket_handler ON ticket(handler_id);
CREATE INDEX IF NOT EXISTS idx_ticket_creator ON ticket(creator_id);
CREATE INDEX IF NOT EXISTS idx_ticket_customer ON ticket(customer_id);
CREATE INDEX IF NOT EXISTS idx_ticket_history_ticket ON ticket_history(ticket_id);
CREATE INDEX IF NOT EXISTS idx_ticket_message_ticket ON ticket_message(ticket_id);
