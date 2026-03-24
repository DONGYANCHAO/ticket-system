package com.ticket.modules.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ticket.modules.system.entity.SlaConfig;

/**
 * SLA配置服务接口
 *
 * @author Ticket System
 */
public interface SlaConfigService extends IService<SlaConfig> {

    /**
     * 分页查询SLA配置
     */
    Page<SlaConfig> pageSla(Page<SlaConfig> page);
}
