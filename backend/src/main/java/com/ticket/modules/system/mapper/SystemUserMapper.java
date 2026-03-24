package com.ticket.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ticket.modules.system.entity.SystemUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 系统用户Mapper
 *
 * @author Ticket System
 */
@Mapper
public interface SystemUserMapper extends BaseMapper<SystemUser> {

    /**
     * 更新最后登录时间
     */
    @Update("UPDATE system_user SET last_login_time = NOW(), last_login_ip = #{ip} WHERE id = #{id}")
    int updateLastLoginTime(@Param("id") Long id, @Param("ip") String ip);
}
