package com.ticket.modules.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ticket.modules.customer.entity.CustomerContact;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户联系人Mapper
 *
 * @author Ticket System
 */
@Mapper
public interface CustomerContactMapper extends BaseMapper<CustomerContact> {
}
