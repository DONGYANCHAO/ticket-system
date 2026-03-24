package com.ticket.modules.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ticket.common.exception.BusinessException;
import com.ticket.modules.customer.entity.CustomerContact;
import com.ticket.modules.customer.mapper.CustomerContactMapper;
import com.ticket.modules.customer.service.CustomerContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 客户联系人服务实现
 *
 * @author Ticket System
 */
@Service
@RequiredArgsConstructor
public class CustomerContactServiceImpl extends ServiceImpl<CustomerContactMapper, CustomerContact>
        implements CustomerContactService {

    @Override
    public List<CustomerContact> getContactsByCustomerId(Long customerId) {
        return list(new LambdaQueryWrapper<CustomerContact>()
                .eq(CustomerContact::getCustomerId, customerId)
                .eq(CustomerContact::getDeleted, 0)
                .orderByDesc(CustomerContact::getIsPrimary)
                .orderByAsc(CustomerContact::getName));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addContact(CustomerContact contact) {
        // 如果设置为主要联系人，先取消其他主要联系人
        if (SystemConstants.YesNo.YES.equals(contact.getIsPrimary())) {
            clearPrimaryFlag(contact.getCustomerId());
        }

        // 如果是第一个联系人，自动设为主要联系人
        Long count = count(new LambdaQueryWrapper<CustomerContact>()
                .eq(CustomerContact::getCustomerId, contact.getCustomerId())
                .eq(CustomerContact::getDeleted, 0));
        if (count == 0) {
            contact.setIsPrimary(SystemConstants.YesNo.YES);
        }

        return save(contact);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateContact(CustomerContact contact) {
        CustomerContact exist = getById(contact.getId());
        if (exist == null) {
            throw new BusinessException("联系人不存在");
        }

        // 如果设置为主要联系人，先取消其他主要联系人
        if (SystemConstants.YesNo.YES.equals(contact.getIsPrimary()) && !SystemConstants.YesNo.YES.equals(exist.getIsPrimary())) {
            clearPrimaryFlag(exist.getCustomerId());
        }

        return updateById(contact);
    }

    @Override
    public boolean deleteContact(Long id) {
        CustomerContact contact = getById(id);
        if (contact == null) {
            throw new BusinessException("联系人不存在");
        }

        boolean removed = removeById(id);

        // 如果删除的是主要联系人，将第一个联系人设为主要联系人
        if (SystemConstants.YesNo.YES.equals(contact.getIsPrimary())) {
            CustomerContact first = getOne(new LambdaQueryWrapper<CustomerContact>()
                    .eq(CustomerContact::getCustomerId, contact.getCustomerId())
                    .eq(CustomerContact::getDeleted, 0)
                    .orderByAsc(CustomerContact::getCreateTime)
                    .last("LIMIT 1"));
            if (first != null) {
                first.setIsPrimary(SystemConstants.YesNo.YES);
                updateById(first);
            }
        }

        return removed;
    }

    /**
     * 清除客户的主要联系人标记
     */
    private void clearPrimaryFlag(Long customerId) {
        CustomerContact primary = getOne(new LambdaQueryWrapper<CustomerContact>()
                .eq(CustomerContact::getCustomerId, customerId)
                .eq(CustomerContact::getIsPrimary, SystemConstants.YesNo.YES)
                .eq(CustomerContact::getDeleted, 0));
        if (primary != null) {
            primary.setIsPrimary(SystemConstants.YesNo.NO);
            updateById(primary);
        }
    }
}
