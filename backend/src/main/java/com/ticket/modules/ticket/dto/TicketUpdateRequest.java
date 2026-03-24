package com.ticket.modules.ticket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 更新工单请求DTO
 *
 * @author Ticket System
 */
@Data
@Schema(description = "更新工单请求")
public class TicketUpdateRequest {

    @Schema(description = "工单标题")
    private String title;

    @Schema(description = "工单类型")
    private String type;

    @Schema(description = "优先级")
    private String priority;

    @Schema(description = "处理人ID")
    private Long handlerId;

    @Schema(description = "问题模块ID")
    private Long moduleId;

    @Schema(description = "问题描述")
    private String description;

    @Schema(description = "是否私有")
    private Boolean isPrivate;

    @Schema(description = "抄送人ID列表")
    private List<Long> ccIds;

    @Schema(description = "标签ID列表")
    private List<Long> tagIds;
}
