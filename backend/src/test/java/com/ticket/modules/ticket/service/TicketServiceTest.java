package com.ticket.modules.ticket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.modules.ticket.dto.TicketCreateRequest;
import com.ticket.modules.ticket.dto.TicketUpdateRequest;
import com.ticket.modules.ticket.entity.Ticket;
import com.ticket.modules.ticket.mapper.TicketMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 工单服务单元测试
 * 使用 Mockito 进行服务层测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("工单服务测试")
class TicketServiceTest {

    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private Ticket testTicket;
    private TicketCreateRequest createRequest;
    private TicketUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testTicket = new Ticket();
        testTicket.setId(1L);
        testTicket.setTitle("测试工单");
        testTicket.setContent("测试内容");
        testTicket.setStatus(0);
        testTicket.setPriority(1);
        testTicket.setCreatorId(1L);
        testTicket.setCustomerId(1L);

        createRequest = new TicketCreateRequest();
        createRequest.setTitle("新建工单");
        createRequest.setContent("工单内容");
        createRequest.setPriority(1);
        createRequest.setCustomerId(1L);

        updateRequest = new TicketUpdateRequest();
        updateRequest.setTitle("更新后的标题");
        updateRequest.setContent("更新后的内容");
    }

    @Test
    @DisplayName("创建工单 - 成功")
    void createTicket_Success() {
        // given
        when(ticketMapper.insert(any(Ticket.class))).thenReturn(1);

        // when
        Map<String, Object> result = ticketService.createTicket(createRequest);

        // then
        assertNotNull(result);
        verify(ticketMapper, times(1)).insert(any(Ticket.class));
    }

    @Test
    @DisplayName("更新工单 - 成功")
    void updateTicket_Success() {
        // given
        when(ticketMapper.selectById(1L)).thenReturn(testTicket);
        when(ticketMapper.updateById(any(Ticket.class))).thenReturn(1);

        // when
        boolean result = ticketService.updateTicket(1L, updateRequest);

        // then
        assertTrue(result);
        verify(ticketMapper, times(1)).selectById(1L);
        verify(ticketMapper, times(1)).updateById(any(Ticket.class));
    }

    @Test
    @DisplayName("更新工单 - 工单不存在")
    void updateTicket_NotFound() {
        // given
        when(ticketMapper.selectById(999L)).thenReturn(null);

        // when & then
        assertThrows(RuntimeException.class, () -> {
            ticketService.updateTicket(999L, updateRequest);
        });
    }

    @Test
    @DisplayName("删除工单 - 成功")
    void deleteTicket_Success() {
        // given
        when(ticketMapper.selectById(1L)).thenReturn(testTicket);
        when(ticketMapper.deleteById(1L)).thenReturn(1);

        // when
        boolean result = ticketService.deleteTicket(1L);

        // then
        assertTrue(result);
        verify(ticketMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("获取工单详情 - 成功")
    void getTicketDetail_Success() {
        // given
        when(ticketMapper.selectById(1L)).thenReturn(testTicket);

        // when
        Map<String, Object> result = ticketService.getTicketDetail(1L);

        // then
        assertNotNull(result);
        verify(ticketMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("分配工单 - 成功")
    void assignTicket_Success() {
        // given
        when(ticketMapper.selectById(1L)).thenReturn(testTicket);
        when(ticketMapper.updateById(any(Ticket.class))).thenReturn(1);

        // when
        boolean result = ticketService.assignTicket(1L, 2L, "分配给工程师处理");

        // then
        assertTrue(result);
        verify(ticketMapper, times(1)).updateById(any(Ticket.class));
    }

    @Test
    @DisplayName("关闭工单 - 成功")
    void closeTicket_Success() {
        // given
        when(ticketMapper.selectById(1L)).thenReturn(testTicket);
        when(ticketMapper.updateById(any(Ticket.class))).thenReturn(1);

        // when
        boolean result = ticketService.closeTicket(1L, "客户要求关闭");

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("解决工单 - 成功")
    void solveTicket_Success() {
        // given
        when(ticketMapper.selectById(1L)).thenReturn(testTicket);
        when(ticketMapper.updateById(any(Ticket.class))).thenReturn(1);

        // when
        boolean result = ticketService.solveTicket(1L, "问题已解决");

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("分页查询工单")
    void pageTicket() {
        // given
        Page<Ticket> page = new Page<>(1, 10);
        Ticket query = new Ticket();
        query.setStatus(0);

        // when
        Page<Ticket> result = ticketService.pageTicket(page, query);

        // then
        assertNotNull(result);
    }

    @Test
    @DisplayName("合并工单 - 成功")
    void mergeTickets_Success() {
        // given
        Ticket mainTicket = new Ticket();
        mainTicket.setId(1L);
        mainTicket.setStatus(0);

        Ticket mergeTicket1 = new Ticket();
        mergeTicket1.setId(2L);
        mergeTicket1.setStatus(0);

        when(ticketMapper.selectById(1L)).thenReturn(mainTicket);
        when(ticketMapper.selectById(2L)).thenReturn(mergeTicket1);
        when(ticketMapper.updateById(any(Ticket.class))).thenReturn(1);

        // when
        boolean result = ticketService.mergeTickets(1L, 2L);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("催单 - 成功")
    void remindTicket_Success() {
        // given
        when(ticketMapper.selectById(1L)).thenReturn(testTicket);
        when(ticketMapper.updateById(any(Ticket.class))).thenReturn(1);

        // when
        boolean result = ticketService.remindTicket(1L);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("撤回工单 - 成功")
    void withdrawTicket_Success() {
        // given
        testTicket.setStatus(0); // 待处理状态
        when(ticketMapper.selectById(1L)).thenReturn(testTicket);
        when(ticketMapper.updateById(any(Ticket.class))).thenReturn(1);

        // when
        boolean result = ticketService.withdrawTicket(1L);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("撤回工单 - 非待处理状态不能撤回")
    void withdrawTicket_InvalidStatus() {
        // given
        testTicket.setStatus(1); // 处理中状态
        when(ticketMapper.selectById(1L)).thenReturn(testTicket);

        // when & then
        assertThrows(RuntimeException.class, () -> {
            ticketService.withdrawTicket(1L);
        });
    }
}
