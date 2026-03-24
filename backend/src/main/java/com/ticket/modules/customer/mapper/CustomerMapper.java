package com.ticket.modules.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ticket.modules.customer.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户Mapper
 *
 * @author Ticket System
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
