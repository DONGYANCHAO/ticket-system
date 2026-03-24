package com.ticket.modules.customer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ticket.modules.customer.entity.Customer;

import java.util.Map;

/**
 * 客户服务接口
 *
 * @author Ticket System
 */
public interface CustomerService extends IService<Customer> {

    /**
     * 分页查询客户列表
     */
    Page<Customer> pageCustomer(Page<Customer> page, Customer query);

    /**
     * 获取客户详情
     */
    Map<String, Object> getCustomerDetail(Long id);

    /**
     * 新增客户
     */
    boolean addCustomer(Customer customer);

    /**
     * 更新客户
     */
    boolean updateCustomer(Customer customer);

    /**
     * 删除客户
     */
    boolean deleteCustomer(Long id);

    /**
     * 获取客户统计信息
     */
    Map<String, Object> getCustomerStatistics(Long id);
}
