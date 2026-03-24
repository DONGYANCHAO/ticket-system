package com.ticket.modules.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ticket.common.constant.SystemConstants;
import com.ticket.common.exception.BusinessException;
import com.ticket.modules.customer.entity.Customer;
import com.ticket.modules.customer.mapper.CustomerMapper;
import com.ticket.modules.customer.service.CustomerService;
import com.ticket.modules.ticket.mapper.TicketMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户服务实现
 *
 * @author Ticket System
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    private final TicketMapper ticketMapper;

    @Override
    public Page<Customer> pageCustomer(Page<Customer> page, Customer query) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getName())) {
            wrapper.like(Customer::getName, query.getName());
        }
        if (StringUtils.hasText(query.getLevel())) {
            wrapper.eq(Customer::getLevel, query.getLevel());
        }
        if (StringUtils.hasText(query.getIndustry())) {
            wrapper.like(Customer::getIndustry, query.getIndustry());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Customer::getStatus, query.getStatus());
        }

        wrapper.orderByDesc(Customer::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public Map<String, Object> getCustomerDetail(Long id) {
        Customer customer = getById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        Map<String, Object> detail = new HashMap<>();
        detail.put("customer", customer);

        // 查询统计信息
        Map<String, Object> stats = getCustomerStatistics(id);
        detail.put("statistics", stats);

        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCustomer(Customer customer) {
        // 检查客户名称是否已存在
        Customer exist = getOne(new LambdaQueryWrapper<Customer>()
                .eq(Customer::getName, customer.getName())
                .eq(Customer::getDeleted, 0));
        if (exist != null) {
            throw new BusinessException("客户名称已存在");
        }

        // 设置默认值
        if (customer.getStatus() == null) {
            customer.setStatus(SystemConstants.Status.ENABLED);
        }
        if (customer.getLevel() == null) {
            customer.setLevel(SystemConstants.ServiceLevel.STANDARD);
        }

        return save(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCustomer(Customer customer) {
        Customer exist = getById(customer.getId());
        if (exist == null) {
            throw new BusinessException("客户不存在");
        }

        // 如果修改了名称，检查唯一性
        if (StringUtils.hasText(customer.getName()) && !customer.getName().equals(exist.getName())) {
            Customer existName = getOne(new LambdaQueryWrapper<Customer>()
                    .eq(Customer::getName, customer.getName())
                    .eq(Customer::getDeleted, 0));
            if (existName != null) {
                throw new BusinessException("客户名称已存在");
            }
        }

        return updateById(customer);
    }

    @Override
    public boolean deleteCustomer(Long id) {
        Customer customer = getById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        // 检查是否有未完成的工单
        Long ticketCount = ticketMapper.countByCustomerId(id);
        if (ticketCount > 0) {
            throw new BusinessException("该客户存在未完成的工单，无法删除");
        }

        return removeById(id);
    }

    @Override
    public Map<String, Object> getCustomerStatistics(Long id) {
        Map<String, Object> stats = new HashMap<>();

        // 工单总数
        Long totalTickets = ticketMapper.countByCustomerId(id);
        stats.put("totalTickets", totalTickets);

        // 已解决工单数
        Long solvedTickets = ticketMapper.countByCustomerIdAndStatus(id, SystemConstants.TicketStatus.SOLVED);
        stats.put("solvedTickets", solvedTickets);

        // 计算解决率
        double solvedRate = totalTickets > 0 ? (double) solvedTickets / totalTickets * 100 : 0;
        stats.put("solvedRate", String.format("%.1f", solvedRate));

        // 平均满意度评分
        Double avgScore = ticketMapper.getAvgSatisfactionScore(id);
        stats.put("avgScore", avgScore != null ? avgScore : 0);

        return stats;
    }
}
