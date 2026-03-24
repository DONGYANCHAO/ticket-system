package com.ticket.modules.ticket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 创建工单请求DTO
 *
 * @author Ticket System
 */
@Data
@Schema(description = "创建工单请求")
public class TicketCreateRequest {

    @Schema(description = "工单标题")
    @NotBlank(message = "工单标题不能为空")
    private String title;

    @Schema(description = "工单类型")
    @NotBlank(message = "工单类型不能为空")
    private String type;

    @Schema(description = "优先级")
    @NotBlank(message = "优先级不能为空")
    private String priority;

    @Schema(description = "客户ID")
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @Schema(description = "问题模块ID")
    private Long moduleId;

    @Schema(description = "问题描述")
    @NotBlank(message = "问题描述不能为空")
    private String description;

    @Schema(description = "处理人ID")
    private Long handlerId;

    @Schema(description = "抄送人ID列表")
    private List<Long> ccIds;

    @Schema(description = "标签ID列表")
    private List<Long> tagIds;

    @Schema(description = "是否私有")
    private Boolean isPrivate = false;

    @Schema(description = "附件URL列表")
    private List<String> attachments;

    @Schema(description = "通知渠道")
    private List<String> notifyChannels;
}
