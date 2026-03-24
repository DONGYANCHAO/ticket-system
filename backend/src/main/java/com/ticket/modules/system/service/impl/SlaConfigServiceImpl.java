package com.ticket.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ticket.modules.system.entity.SlaConfig;
import com.ticket.modules.system.mapper.SlaConfigMapper;
import com.ticket.modules.system.service.SlaConfigService;
import org.springframework.stereotype.Service;

/**
 * SLA配置服务实现
 *
 * @author Ticket System
 */
@Service
public class SlaConfigServiceImpl extends ServiceImpl<SlaConfigMapper, SlaConfig> implements SlaConfigService {

    @Override
    public Page<SlaConfig> pageSla(Page<SlaConfig> page) {
        return page(page, new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SlaConfig>()
                .orderByDesc(SlaConfig::getCreateTime));
    }
}
