package com.ticket.modules.ticket.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ticket.common.constant.SystemConstants;
import com.ticket.common.util.SecurityUtils;
import com.ticket.modules.system.entity.SystemUser;
import com.ticket.modules.system.mapper.SystemUserMapper;
import com.ticket.modules.ticket.entity.TicketHistory;
import com.ticket.modules.ticket.mapper.TicketHistoryMapper;
import com.ticket.modules.ticket.service.TicketHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 工单历史服务实现
 *
 * @author Ticket System
 */
@Service
@RequiredArgsConstructor
public class TicketHistoryServiceImpl extends ServiceImpl<TicketHistoryMapper, TicketHistory>
        implements TicketHistoryService {

    private final SystemUserMapper userMapper;

    @Override
    public Page<TicketHistory> pageHistory(Page<TicketHistory> page, Long ticketId) {
        return ticketMapper.selectHistoryPage(page, ticketId);
    }

    @Override
    public void recordHistory(Long ticketId, String action, String content, String oldValue, String newValue) {
        TicketHistory history = new TicketHistory();
        history.setTicketId(ticketId);
        history.setAction(action);
        history.setContent(content);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        setOperatorInfo(history);
        save(history);
    }

    @Override
    public void recordStatusChange(Long ticketId, String oldStatus, String newStatus) {
        recordHistory(ticketId, "STATUS_CHANGE",
                "状态从 [" + getStatusText(oldStatus) + "] 变更为 [" + getStatusText(newStatus) + "]",
                oldStatus, newStatus);
    }

    @Override
    public void recordAssign(Long ticketId, Long oldHandlerId, Long newHandlerId) {
        String oldName = null;
        if (oldHandlerId != null) {
            SystemUser oldHandler = userMapper.selectById(oldHandlerId);
            oldName = oldHandler != null ? oldHandler.getRealName() : "未知";
        }

        SystemUser newHandler = userMapper.selectById(newHandlerId);
        String newName = newHandler != null ? newHandler.getRealName() : "未知";

        recordHistory(ticketId, "ASSIGN",
                "分配给 " + newName,
                oldName, newName);
    }

    @Override
    public void recordReply(Long ticketId, Long senderId, String senderType, String type) {
        String senderName = null;
        if (senderId != null) {
            SystemUser sender = userMapper.selectById(senderId);
            senderName = sender != null ? sender.getRealName() : "未知用户";
        }

        String typeText = SystemConstants.MessageType.INTERNAL.equals(type) ? "内部备注" : "公开回复";
        recordHistory(ticketId, "REPLY", senderName + " 发送了" + typeText, null, null);
    }

    /**
     * 设置操作人信息
     */
    private void setOperatorInfo(TicketHistory history) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != null) {
            history.setOperatorId(currentUserId);
            SystemUser user = userMapper.selectById(currentUserId);
            if (user != null) {
                history.setOperatorName(user.getRealName());
            }
            history.setOperatorType(SystemConstants.SenderType.SUPPORT);
        } else {
            history.setOperatorType(SystemConstants.SenderType.CUSTOMER);
        }
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(String status) {
        if (status == null) return "未知";
        return switch (status) {
            case "NEW" -> "新建";
            case "CONFIRMED" -> "已确认";
            case "PROCESSING" -> "处理中";
            case "PENDING_VERIFY" -> "待验证";
            case "SOLVED" -> "已解决";
            case "CLOSED" -> "已关闭";
            case "WITHDRAWN" -> "已撤回";
            case "MERGED" -> "已合并";
            default -> status;
        };
    }
}
