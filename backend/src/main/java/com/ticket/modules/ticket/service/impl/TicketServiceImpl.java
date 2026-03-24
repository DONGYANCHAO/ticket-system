package com.ticket.modules.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ticket.common.constant.SystemConstants;
import com.ticket.common.exception.BusinessException;
import com.ticket.common.util.SecurityUtils;
import com.ticket.modules.customer.entity.Customer;
import com.ticket.modules.customer.mapper.CustomerMapper;
import com.ticket.modules.system.entity.SystemUser;
import com.ticket.modules.system.mapper.SystemUserMapper;
import com.ticket.modules.ticket.dto.TicketCreateRequest;
import com.ticket.modules.ticket.dto.TicketUpdateRequest;
import com.ticket.modules.ticket.entity.Ticket;
import com.ticket.modules.ticket.entity.TicketHistory;
import com.ticket.modules.ticket.entity.TicketTag;
import com.ticket.modules.ticket.mapper.TicketHistoryMapper;
import com.ticket.modules.ticket.mapper.TicketMapper;
import com.ticket.modules.ticket.mapper.TicketTagMapper;
import com.ticket.modules.ticket.service.TicketHistoryService;
import com.ticket.modules.ticket.service.TicketMessageService;
import com.ticket.modules.ticket.service.TicketService;
import com.ticket.modules.ticket.service.TicketTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工单服务实现
 *
 * @author Ticket System
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket> implements TicketService {

    private final TicketMapper ticketMapper;
    private final TicketTagMapper ticketTagMapper;
    private final TicketHistoryMapper ticketHistoryMapper;
    private final CustomerMapper customerMapper;
    private final SystemUserMapper userMapper;
    private final TicketTagService ticketTagService;
    private final TicketHistoryService ticketHistoryService;
    private final TicketMessageService ticketMessageService;

    @Override
    public Page<Ticket> pageTicket(Page<Ticket> page, Ticket query) {
        return ticketMapper.selectTicketPage(page, query);
    }

    @Override
    public Map<String, Object> getTicketDetail(Long id) {
        Ticket ticket = getById(id);
        if (ticket == null) {
            throw new BusinessException("工单不存在");
        }

        Map<String, Object> detail = new HashMap<>();

        // 工单基本信息
        detail.put("ticket", ticket);

        // 客户信息
        if (ticket.getCustomerId() != null) {
            Customer customer = customerMapper.selectById(ticket.getCustomerId());
            detail.put("customer", customer);
        }

        // 创建人信息
        if (ticket.getCreatorId() != null) {
            SystemUser creator = userMapper.selectById(ticket.getCreatorId());
            detail.put("creator", creator);
        }

        // 处理人信息
        if (ticket.getHandlerId() != null) {
            SystemUser handler = userMapper.selectById(ticket.getHandlerId());
            detail.put("handler", handler);
        }

        // 标签列表
        List<TicketTag> tags = ticketTagService.getTagsByTicketId(id);
        detail.put("tags", tags);

        // 处理历史
        List<TicketHistory> history = ticketHistoryService.list(
                new LambdaQueryWrapper<TicketHistory>()
                        .eq(TicketHistory::getTicketId, id)
                        .orderByAsc(TicketHistory::getCreateTime)
        );
        detail.put("history", history);

        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createTicket(TicketCreateRequest request) {
        // 生成工单编号
        String ticketNo = generateTicketNo();

        // 获取客户信息
        Customer customer = customerMapper.selectById(request.getCustomerId());
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        // 设置SLA
        Integer slaFirstResponseMinutes = 240; // 默认4小时
        Integer slaResolveMinutes = 1440; // 默认24小时
        if (customer.getSlaId() != null) {
            // TODO: 从SLA配置中获取时限
        }

        // 创建工单
        Ticket ticket = new Ticket();
        BeanUtils.copyProperties(request, ticket);
        ticket.setTicketNo(ticketNo);
        ticket.setStatus(SystemConstants.TicketStatus.NEW);
        ticket.setCreatorId(SecurityUtils.getCurrentUserId());
        ticket.setSlaFirstResponseMinutes(slaFirstResponseMinutes);
        ticket.setSlaResolveMinutes(slaResolveMinutes);
        ticket.setSlaWarning(0);

        save(ticket);

        // 添加工单标签
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            ticketTagService.addTicketTags(ticket.getId(), request.getTagIds());
        }

        // 记录创建历史
        ticketHistoryService.recordHistory(ticket.getId(), "CREATE", "创建了工单", null, null);

        // 如果指定了处理人，自动分配
        if (request.getHandlerId() != null) {
            assignTicket(ticket.getId(), request.getHandlerId(), "创建时指定");
        }

        log.info("工单创建成功: {}, 客户: {}", ticketNo, customer.getName());

        Map<String, Object> result = new HashMap<>();
        result.put("id", ticket.getId());
        result.put("ticketNo", ticketNo);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTicket(Long id, TicketUpdateRequest request) {
        Ticket ticket = getById(id);
        if (ticket == null) {
            throw new BusinessException("工单不存在");
        }

        // 检查状态
        if (SystemConstants.TicketStatus.SOLVED.equals(ticket.getStatus()) ||
                SystemConstants.TicketStatus.CLOSED.equals(ticket.getStatus())) {
            throw new BusinessException("已关闭的工单不能修改");
        }

        // 记录变更
        if (StringUtils.hasText(request.getTitle()) && !request.getTitle().equals(ticket.getTitle())) {
            ticketHistoryService.recordHistory(id, "UPDATE", "修改了标题", ticket.getTitle(), request.getTitle());
        }
        if (StringUtils.hasText(request.getPriority()) && !request.getPriority().equals(ticket.getPriority())) {
            ticketHistoryService.recordHistory(id, "UPDATE", "修改了优先级", ticket.getPriority(), request.getPriority());
        }

        // 更新字段
        BeanUtils.copyProperties(request, ticket);
        ticket.setId(id);
        ticket.setUpdateBy(SecurityUtils.getCurrentUserId());

        boolean result = updateById(ticket);

        // 更新标签
        if (request.getTagIds() != null) {
            ticketTagService.updateTicketTags(id, request.getTagIds());
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTicket(Long id) {
        Ticket ticket = getById(id);
        if (ticket == null) {
            throw new BusinessException("工单不存在");
        }

        // 只有创建人或管理员可以删除
        Long currentUserId = SecurityUtils.getCurrentUserId();
        SystemUser currentUser = userMapper.selectById(currentUserId);
        if (!SystemConstants.Role.ADMIN.equals(currentUser.getRole()) &&
                !ticket.getCreatorId().equals(currentUserId)) {
            throw new BusinessException("无权删除此工单");
        }

        // 检查是否有未关闭的工单
        if (!SystemConstants.TicketStatus.SOLVED.equals(ticket.getStatus()) &&
                !SystemConstants.TicketStatus.CLOSED.equals(ticket.getStatus()) &&
                !SystemConstants.TicketStatus.WITHDRAWN.equals(ticket.getStatus())) {
            throw new BusinessException("请先关闭或解决工单后再删除");
        }

        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignTicket(Long id, Long handlerId, String remark) {
        Ticket ticket = getById(id);
        if (ticket == null) {
            throw new BusinessException("工单不存在");
        }

        SystemUser newHandler = userMapper.selectById(handlerId);
        if (newHandler == null) {
            throw new BusinessException("处理人不存在");
        }

        Long oldHandlerId = ticket.getHandlerId();
        String oldHandlerName = null;
        if (oldHandlerId != null) {
            SystemUser oldHandler = userMapper.selectById(oldHandlerId);
            oldHandlerName = oldHandler != null ? oldHandler.getRealName() : null;
        }

        ticket.setHandlerId(handlerId);

        // 如果是新工单或已确认状态，改为处理中
        if (SystemConstants.TicketStatus.NEW.equals(ticket.getStatus())) {
            ticket.setStatus(SystemConstants.TicketStatus.PROCESSING);
        } else if (SystemConstants.TicketStatus.CONFIRMED.equals(ticket.getStatus())) {
            ticket.setStatus(SystemConstants.TicketStatus.PROCESSING);
        }

        boolean result = updateById(ticket);

        // 记录历史
        ticketHistoryService.recordAssign(id, oldHandlerId, handlerId);

        log.info("工单分配: {} -> {}", oldHandlerName, newHandler.getRealName());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeTicket(Long id, String reason) {
        Ticket ticket = getById(id);
        if (ticket == null) {
            throw new BusinessException("工单不存在");
        }

        ticket.setStatus(SystemConstants.TicketStatus.CLOSED);
        ticket.setCloseTime(LocalDateTime.now());
        ticket.setCloseReason(reason);

        boolean result = updateById(ticket);

        // 记录历史
        ticketHistoryService.recordStatusChange(id, ticket.getStatus(), SystemConstants.TicketStatus.CLOSED);

        log.info("工单关闭: {}, 原因: {}", ticket.getTicketNo(), reason);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean withdrawTicket(Long id) {
        Ticket ticket = getById(id);
        if (ticket == null) {
            throw new BusinessException("工单不存在");
        }

        // 只有创建人可以撤回
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!ticket.getCreatorId().equals(currentUserId)) {
            throw new BusinessException("只有创建人可以撤回工单");
        }

        // 检查状态
        if (!SystemConstants.TicketStatus.NEW.equals(ticket.getStatus()) &&
                !SystemConstants.TicketStatus.CONFIRMED.equals(ticket.getStatus())) {
            throw new BusinessException("工单已开始处理，无法撤回");
        }

        ticket.setStatus(SystemConstants.TicketStatus.WITHDRAWN);
        boolean result = updateById(ticket);

        // 记录历史
        ticketHistoryService.recordStatusChange(id, ticket.getStatus(), SystemConstants.TicketStatus.WITHDRAWN);

        log.info("工单撤回: {}", ticket.getTicketNo());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean startProcess(Long id) {
        Ticket ticket = getById(id);
        if (ticket == null) {
            throw new BusinessException("工单不存在");
        }

        if (!SystemConstants.TicketStatus.CONFIRMED.equals(ticket.getStatus())) {
            throw new BusinessException("只有已确认的工单才能开始处理");
        }

        ticket.setStatus(SystemConstants.TicketStatus.PROCESSING);

        // 设置首次响应时间
        if (ticket.getFirstResponseTime() == null) {
            ticket.setFirstResponseTime(LocalDateTime.now());
        }

        boolean result = updateById(ticket);

        // 记录历史
        ticketHistoryService.recordStatusChange(id, SystemConstants.TicketStatus.CONFIRMED, SystemConstants.TicketStatus.PROCESSING);

        log.info("工单开始处理: {}", ticket.getTicketNo());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pendingVerify(Long id, String remark) {
        Ticket ticket = getById(id);
        if (ticket == null) {
            throw new BusinessException("工单不存在");
        }

        ticket.setStatus(SystemConstants.TicketStatus.PENDING_VERIFY);
        boolean result = updateById(ticket);

        // 记录历史
        ticketHistoryService.recordHistory(id, "PENDING_VERIFY", "标记为待验证" + (remark != null ? ": " + remark : ""), null, null);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean solveTicket(Long id, String remark) {
        Ticket ticket = getById(id);
        if (ticket == null) {
            throw new BusinessException("工单不存在");
        }

        ticket.setStatus(SystemConstants.TicketStatus.SOLVED);
        ticket.setSolveTime(LocalDateTime.now());
        if (remark != null) {
            ticket.setSolveRemark(remark);
        }

        boolean result = updateById(ticket);

        // 记录历史
        ticketHistoryService.recordStatusChange(id, ticket.getStatus(), SystemConstants.TicketStatus.SOLVED);

        log.info("工单已解决: {}", ticket.getTicketNo());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean mergeTickets(Long mainId, Long... mergeIds) {
        if (mergeIds == null || mergeIds.length == 0) {
            throw new BusinessException("请选择要合并的工单");
        }

        Ticket mainTicket = getById(mainId);
        if (mainTicket == null) {
            throw new BusinessException("主工单不存在");
        }

        for (Long mergeId : mergeIds) {
            Ticket ticket = getById(mergeId);
            if (ticket == null) {
                continue;
            }

            ticket.setStatus(SystemConstants.TicketStatus.MERGED);
            updateById(ticket);

            // 记录历史
            ticketHistoryService.recordHistory(mergeId, "MERGE", "合并到工单 " + mainTicket.getTicketNo(), null, mainId.toString());
        }

        // 记录主工单历史
        ticketHistoryService.recordHistory(mainId, "MERGE", "合并了 " + mergeIds.length + " 个工单", null, Arrays.stream(mergeIds).map(String::valueOf).collect(Collectors.joining(",")));

        log.info("工单合并: {} <- {}", mainTicket.getTicketNo(), Arrays.toString(mergeIds));

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean splitTicket(Long id, TicketCreateRequest request) {
        Ticket originalTicket = getById(id);
        if (originalTicket == null) {
            throw new BusinessException("原工单不存在");
        }

        // 创建新工单
        request.setCustomerId(originalTicket.getCustomerId());
        request.setCreatorId(SecurityUtils.getCurrentUserId());

        Map<String, Object> result = createTicket(request);

        // 记录历史
        ticketHistoryService.recordHistory(id, "SPLIT", "拆分为工单 " + result.get("ticketNo"), null, result.get("id").toString());
        ticketHistoryService.recordHistory((Long) result.get("id"), "SPLIT", "从工单 " + originalTicket.getTicketNo() + " 拆分", null, id.toString());

        log.info("工单拆分: {} -> {}", originalTicket.getTicketNo(), result.get("ticketNo"));

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean remindTicket(Long id) {
        Ticket ticket = getById(id);
        if (ticket == null) {
            throw new BusinessException("工单不存在");
        }

        // 记录催单历史
        ticketHistoryService.recordHistory(id, "REMIND", "客户催单", null, null);

        // TODO: 发送催单通知

        log.info("工单催单: {}", ticket.getTicketNo());

        return true;
    }

    @Override
    public Page<Ticket> getTodoTickets(Page<Ticket> page) {
        Long userId = SecurityUtils.getCurrentUserId();
        Ticket query = new Ticket();
        // 查询分配给当前用户的处理中工单
        query.setHandlerId(userId);
        return pageTicket(page, query);
    }

    @Override
    public Page<Ticket> getMyCreatedTickets(Page<Ticket> page) {
        Long userId = SecurityUtils.getCurrentUserId();
        Ticket query = new Ticket();
        query.setCreatorId(userId);
        return pageTicket(page, query);
    }

    @Override
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("todayNew", ticketMapper.countTodayNew());
        stats.put("processing", ticketMapper.countProcessing());
        stats.put("solvedThisMonth", ticketMapper.countSolvedThisMonth());
        stats.put("slaWarning", ticketMapper.countSlaWarning());

        // TODO: 查询近7天的工单趋势数据

        return stats;
    }

    /**
     * 生成工单编号
     */
    private String generateTicketNo() {
        String prefix = "TK";
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 查询当天工单数量
        Long count = count(new LambdaQueryWrapper<Ticket>()
                .apply("DATE(create_time) = CURDATE()"));
        String seq = String.format("%04d", count + 1);
        return prefix + date + seq;
    }
}
