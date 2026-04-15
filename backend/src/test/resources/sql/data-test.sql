-- ==========================================
-- H2 测试数据库初始数据
-- ==========================================

-- 插入测试用户
INSERT INTO sys_user (id, username, password, email, phone, status, created_time) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 'admin@example.com', '13800138000', 1, CURRENT_TIMESTAMP),
(2, 'operator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 'operator@example.com', '13800138001', 1, CURRENT_TIMESTAMP),
(3, 'customer_service', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 'cs@example.com', '13800138002', 1, CURRENT_TIMESTAMP);

-- 插入测试客户
INSERT INTO customer (id, name, email, phone, company, level, status, created_time) VALUES
(1, '测试客户A', 'customer.a@example.com', '13900139000', '测试公司A', 1, 1, CURRENT_TIMESTAMP),
(2, '测试客户B', 'customer.b@example.com', '13900139001', '测试公司B', 2, 1, CURRENT_TIMESTAMP),
(3, '测试客户C', 'customer.c@example.com', '13900139002', '测试公司C', 0, 1, CURRENT_TIMESTAMP);

-- 插入测试工单
INSERT INTO ticket (id, title, content, status, priority, type, source, creator_id, handler_id, customer_id, created_time, updated_time) VALUES
(1, '系统登录问题', '无法登录系统，提示密码错误', 0, 2, '技术支持', 'web', 1, NULL, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '数据导出失败', '导出Excel时提示内存不足', 1, 1, '功能咨询', 'email', 2, 2, 2, CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP),
(3, '界面显示异常', '页面布局错乱', 2, 0, 'Bug反馈', 'phone', 1, 3, 1, CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP),
(4, '功能建议', '建议增加批量操作功能', 3, 0, '功能建议', 'web', 2, 2, 3, CURRENT_TIMESTAMP - INTERVAL '3' DAY, CURRENT_TIMESTAMP),
(5, '账户权限问题', '无法访问某些菜单', 4, 1, '权限问题', 'email', 3, 1, 2, CURRENT_TIMESTAMP - INTERVAL '5' DAY, CURRENT_TIMESTAMP);

-- 插入工单历史记录
INSERT INTO ticket_history (ticket_id, operation, old_value, new_value, operator_id, remark, created_time) VALUES
(1, '创建', NULL, '待处理', 1, '工单创建', CURRENT_TIMESTAMP),
(2, '创建', NULL, '待处理', 2, '工单创建', CURRENT_TIMESTAMP - INTERVAL '1' DAY),
(2, '分配', '待处理', '处理中', 1, '分配给 operator', CURRENT_TIMESTAMP - INTERVAL '12' HOUR),
(3, '创建', NULL, '待处理', 1, '工单创建', CURRENT_TIMESTAMP - INTERVAL '2' DAY),
(3, '分配', '待处理', '处理中', 1, '分配给 customer_service', CURRENT_TIMESTAMP - INTERVAL '1' DAY),
(3, '处理完成', '处理中', '待验证', 3, '问题已修复，请验证', CURRENT_TIMESTAMP - INTERVAL '2' HOUR);

-- 插入工单消息
INSERT INTO ticket_message (ticket_id, content, sender_id, sender_type, message_type, is_internal, created_time) VALUES
(1, '您好，我无法登录系统，请问怎么解决？', 1, 0, 0, 0, CURRENT_TIMESTAMP),
(1, '您好，请确认您的用户名和密码是否正确，注意区分大小写。', 2, 1, 0, 0, CURRENT_TIMESTAMP + INTERVAL '5' MINUTE),
(2, '导出数据时提示内存不足', 2, 0, 0, 0, CURRENT_TIMESTAMP - INTERVAL '1' DAY),
(2, '收到，正在排查问题', 2, 1, 0, 0, CURRENT_TIMESTAMP - INTERVAL '23' HOUR),
(2, '建议分批导出数据，每次不超过1000条', 2, 1, 0, 0, CURRENT_TIMESTAMP - INTERVAL '12' HOUR);

-- 插入 SLA 配置
INSERT INTO sla_config (id, name, priority, response_time, resolve_time, status, created_time) VALUES
(1, '紧急工单 SLA', 3, 15, 240, 1, CURRENT_TIMESTAMP),
(2, '高优先级 SLA', 2, 60, 480, 1, CURRENT_TIMESTAMP),
(3, '中优先级 SLA', 1, 240, 1440, 1, CURRENT_TIMESTAMP),
(4, '低优先级 SLA', 0, 480, 2880, 1, CURRENT_TIMESTAMP);
