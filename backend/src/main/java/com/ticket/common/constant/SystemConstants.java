package com.ticket.common.constant;

/**
 * 系统常量定义
 *
 * @author Ticket System
 */
public class SystemConstants {

    /**
     * 角色常量
     */
    public static class Role {
        /** 管理员 */
        public static final String ADMIN = "ADMIN";
        /** 技术支持 */
        public static final String SUPPORT = "SUPPORT";
        /** 客户成功 */
        public static final String CS = "CS";
        /** 客户用户 */
        public static final String CUSTOMER = "CUSTOMER";

        private Role() {}
    }

    /**
     * 工单状态常量
     */
    public static class TicketStatus {
        /** 新建 */
        public static final String NEW = "NEW";
        /** 已确认 */
        public static final String CONFIRMED = "CONFIRMED";
        /** 处理中 */
        public static final String PROCESSING = "PROCESSING";
        /** 待验证 */
        public static final String PENDING_VERIFY = "PENDING_VERIFY";
        /** 已解决 */
        public static final String SOLVED = "SOLVED";
        /** 已关闭 */
        public static final String CLOSED = "CLOSED";
        /** 已撤回 */
        public static final String WITHDRAWN = "WITHDRAWN";
        /** 已合并 */
        public static final String MERGED = "MERGED";

        private TicketStatus() {}
    }

    /**
     * 工单优先级常量
     */
    public static class Priority {
        /** 紧急 */
        public static final String URGENT = "URGENT";
        /** 高 */
        public static final String HIGH = "HIGH";
        /** 中 */
        public static final String MEDIUM = "MEDIUM";
        /** 低 */
        public static final String LOW = "LOW";

        private Priority() {}
    }

    /**
     * 工单类型常量
     */
    public static class TicketType {
        /** Bug */
        public static final String BUG = "BUG";
        /** 咨询 */
        public static final String CONSULT = "CONSULT";
        /** 需求 */
        public static final String DEMAND = "DEMAND";
        /** 投诉 */
        public static final String COMPLAINT = "COMPLAINT";

        private TicketType() {}
    }

    /**
     * 消息类型常量
     */
    public static class MessageType {
        /** 公开回复 */
        public static final String PUBLIC = "PUBLIC";
        /** 内部备注 */
        public static final String INTERNAL = "INTERNAL";

        private MessageType() {}
    }

    /**
     * 消息发送者类型
     */
    public static class SenderType {
        /** 客户 */
        public static final String CUSTOMER = "CUSTOMER";
        /** 技术支持 */
        public static final String SUPPORT = "SUPPORT";

        private SenderType() {}
    }

    /**
     * 满意度评价维度
     */
    public static class SatisfactionRating {
        /** 满意 */
        public static final String SATISFIED = "SATISFIED";
        /** 一般 */
        public static final String AVERAGE = "AVERAGE";
        /** 不满意 */
        public static final String DISSATISFIED = "DISSATISFIED";

        private SatisfactionRating() {}
    }

    /**
     * 工单关联类型
     */
    public static class RelationType {
        /** 相同问题 */
        public static final String SAME_ISSUE = "SAME_ISSUE";
        /** 父子关联 */
        public static final String PARENT_CHILD = "PARENT_CHILD";
        /** 关联引用 */
        public static final String REFERENCE = "REFERENCE";

        private RelationType() {}
    }

    /**
     * 通知渠道
     */
    public static class NotificationChannel {
        /** 邮件 */
        public static final String EMAIL = "EMAIL";
        /** 站内信 */
        public static final String INNER = "INNER";
        /** 钉钉 */
        public static final String DINGTALK = "DINGTALK";
        /** 短信 */
        public static final String SMS = "SMS";

        private NotificationChannel() {}
    }

    /**
     * 通知事件类型
     */
    public static class EventType {
        /** 工单创建 */
        public static final String TICKET_CREATED = "TICKET_CREATED";
        /** 工单分配 */
        public static final String TICKET_ASSIGNED = "TICKET_ASSIGNED";
        /** 工单转移 */
        public static final String TICKET_TRANSFERRED = "TICKET_TRANSFERRED";
        /** 状态变更 */
        public static final String STATUS_CHANGED = "STATUS_CHANGED";
        /** SLA即将超时 */
        public static final String SLA_APPROACHING = "SLA_APPROACHING";
        /** SLA超时 */
        public static final String SLA_EXPIRED = "SLA_EXPIRED";
        /** 客户催单 */
        public static final String TICKET_REMINDED = "TICKET_REMINDED";
        /** 工单退回 */
        public static final String TICKET_RETURNED = "TICKET_RETURNED";
        /** 满意度邀请 */
        public static final String FEEDBACK_INVITE = "FEEDBACK_INVITE";
        /** 差评预警 */
        public static final String LOW_RATING_ALERT = "LOW_RATING_ALERT";
        /** 知识文章更新 */
        public static final String ARTICLE_UPDATED = "ARTICLE_UPDATED";

        private EventType() {}
    }

    /**
     * 操作日志模块
     */
    public static class LogModule {
        /** 系统管理 */
        public static final String SYSTEM = "SYSTEM";
        /** 客户管理 */
        public static final String CUSTOMER = "CUSTOMER";
        /** 工单管理 */
        public static final String TICKET = "TICKET";
        /** 知识库 */
        public static final String KNOWLEDGE = "KNOWLEDGE";
        /** 报表 */
        public static final String REPORT = "REPORT";

        private LogModule() {}
    }

    /**
     * 启用状态
     */
    public static class Status {
        /** 禁用 */
        public static final Integer DISABLED = 0;
        /** 启用 */
        public static final Integer ENABLED = 1;

        private Status() {}
    }

    /**
     * 是/否
     */
    public static class YesNo {
        /** 否 */
        public static final Integer NO = 0;
        /** 是 */
        public static final Integer YES = 1;

        private YesNo() {}
    }

    /**
     * 知识库可见性
     */
    public static class Visibility {
        /** 全部可见 */
        public static final String ALL = "ALL";
        /** 仅对内 */
        public static final String INTERNAL = "INTERNAL";
        /** 对客户可见 */
        public static final String EXTERNAL = "EXTERNAL";

        private Visibility() {}
    }

    /**
     * 知识库状态
     */
    public static class ArticleStatus {
        /** 草稿 */
        public static final String DRAFT = "DRAFT";
        /** 已发布 */
        public static final String PUBLISHED = "PUBLISHED";
        /** 已归档 */
        public static final String ARCHIVED = "ARCHIVED";

        private ArticleStatus() {}
    }

    /**
     * 报表调度类型
     */
    public static class ScheduleType {
        /** 每日 */
        public static final String DAILY = "DAILY";
        /** 每周 */
        public static final String WEEKLY = "WEEKLY";
        /** 每月 */
        public static final String MONTHLY = "MONTHLY";

        private ScheduleType() {}
    }

    /**
     * 服务等级
     */
    public static class ServiceLevel {
        /** 标准 */
        public static final String STANDARD = "STANDARD";
        /** 高级 */
        public static final String ADVANCED = "ADVANCED";
        /** 旗舰 */
        public static final String PREMIUM = "PREMIUM";

        private ServiceLevel() {}
    }

    /**
     * 企业规模
     */
    public static class EnterpriseScale {
        /** 小型 */
        public static final String SMALL = "SMALL";
        /** 中型 */
        public static final String MEDIUM = "MEDIUM";
        /** 大型 */
        public static final String LARGE = "LARGE";

        private EnterpriseScale() {}
    }
}
