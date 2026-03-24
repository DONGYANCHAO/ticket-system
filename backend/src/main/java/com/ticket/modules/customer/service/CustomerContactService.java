package com.ticket.modules.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ticket.modules.customer.entity.CustomerContact;

import java.util.List;

/**
 * 客户联系人服务接口
 *
 * @author Ticket System
 */
public interface CustomerContactService extends IService<CustomerContact> {

    /**
     * 获取客户联系人列表
     */
    List<CustomerContact> getContactsByCustomerId(Long customerId);

    /**
     * 新增联系人
     */
    boolean addContact(CustomerContact contact);

    /**
     * 更新联系人
     */
    boolean updateContact(CustomerContact contact);

    /**
     * 删除联系人
     */
    boolean deleteContact(Long id);
}
