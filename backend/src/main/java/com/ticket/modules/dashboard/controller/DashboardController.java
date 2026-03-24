package com.ticket.modules.dashboard.controller;

import com.ticket.common.result.Result;
import com.ticket.modules.ticket.mapper.TicketMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 仪表盘控制器
 *
 * @author Ticket System
 */
@Tag(name = "仪表盘")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final TicketMapper ticketMapper;

    /**
     * 获取仪表盘统计数据
     */
    @Operation(summary = "获取仪表盘统计数据")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 基本统计
        statistics.put("todayNew", ticketMapper.countTodayNew());
        statistics.put("processing", ticketMapper.countProcessing());
        statistics.put("solvedThisMonth", ticketMapper.countSolvedThisMonth());
        statistics.put("slaWarning", ticketMapper.countSlaWarning());

        // 近7天工单趋势（模拟数据）
        statistics.put("trend", getTrendData());

        return Result.success(statistics);
    }

    /**
     * 获取待办事项
     */
    @Operation(summary = "获取待办事项")
    @GetMapping("/todo")
    public Result<List<Map<String, Object>>> getTodoList() {
        List<Map<String, Object>> todoList = new ArrayList<>();

        // 模拟待办数据
        Map<String, Object> item1 = new HashMap<>();
        item1.put("id", 1);
        item1.put("title", "用户无法登录系统");
        item1.put("priority", "URGENT");
        item1.put("customerName", "某某科技");
        item1.put("createTime", "2024-03-24 10:30:00");
        todoList.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("id", 2);
        item2.put("title", "报表导出功能异常");
        item2.put("priority", "HIGH");
        item2.put("customerName", "某某贸易");
        item2.put("createTime", "2024-03-24 11:00:00");
        todoList.add(item2);

        return Result.success(todoList);
    }

    /**
     * 获取类型分布
     */
    @Operation(summary = "获取工单类型分布")
    @GetMapping("/type-distribution")
    public Result<List<Map<String, Object>>> getTypeDistribution() {
        List<Map<String, Object>> distribution = new ArrayList<>();

        distribution.add(createDistributionItem("BUG", 45, "#F56C6C"));
        distribution.add(createDistributionItem("咨询", 30, "#409EFF"));
        distribution.add(createDistributionItem("需求", 15, "#67C23A"));
        distribution.add(createDistributionItem("投诉", 10, "#E6A23C"));

        return Result.success(distribution);
    }

    /**
     * 获取处理人分布
     */
    @Operation(summary = "获取处理人分布")
    @GetMapping("/handler-distribution")
    public Result<List<Map<String, Object>>> getHandlerDistribution() {
        List<Map<String, Object>> distribution = new ArrayList<>();

        distribution.add(createDistributionItem("张三", 12, null));
        distribution.add(createDistributionItem("李四", 8, null));
        distribution.add(createDistributionItem("王五", 15, null));
        distribution.add(createDistributionItem("赵六", 5, null));

        return Result.success(distribution);
    }

    /**
     * 获取满意度分布
     */
    @Operation(summary = "获取满意度分布")
    @GetMapping("/satisfaction-distribution")
    public Result<List<Map<String, Object>>> getSatisfactionDistribution() {
        List<Map<String, Object>> distribution = new ArrayList<>();

        distribution.add(createDistributionItem("满意", 60, "#67C23A"));
        distribution.add(createDistributionItem("一般", 30, "#E6A23C"));
        distribution.add(createDistributionItem("不满意", 10, "#F56C6C"));

        return Result.success(distribution);
    }

    private List<Map<String, Object>> getTrendData() {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 6; i >= 0; i--) {
            LocalDateTime date = now.minusDays(i);
            Map<String, Object> day = new HashMap<>();
            day.put("date", date.toLocalDate().toString());
            day.put("new", (int) (Math.random() * 20) + 5);
            day.put("solved", (int) (Math.random() * 18) + 3);
            trend.add(day);
        }

        return trend;
    }

    private Map<String, Object> createDistributionItem(String name, int value, String color) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("value", value);
        if (color != null) {
            item.put("color", color);
        }
        return item;
    }
}
