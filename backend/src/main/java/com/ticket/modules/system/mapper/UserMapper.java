package com.ticket.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ticket.modules.system.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 *
 * @author Ticket System
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(String username);

    /**
     * 根据客户ID查询用户列表
     */
    java.util.List<User> selectByCustomerId(Long customerId);
}
