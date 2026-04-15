package com.ticket.modules.ticket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.modules.ticket.dto.TicketCreateRequest;
import com.ticket.modules.ticket.dto.TicketUpdateRequest;
import com.ticket.modules.ticket.entity.Ticket;
import com.ticket.modules.ticket.mapper.TicketMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private TicketService ticketService;

    private Ticket testTicket;

    @BeforeEach
    void setUp() {
        testTicket = new Ticket();
        testTicket.setId(1L);
        testTicket.setTitle("测试工单");
        testTicket.setContent("测试内容");
        testTicket.setStatus(1);
        testTicket.setPriority(2);
        testTicket.setCreatorId(1L);
    }

    @Test
    void testPageTicket() {
        Page<Ticket> page = new Page<>(1, 10);
        when(ticketMapper.selectPage(any(Page.class), any())).thenReturn(page);

        Page<Ticket> result = ticketService.pageTicket(page, new Ticket());

        assertNotNull(result);
        assertEquals(1, result.getCurrent());
        assertEquals(10, result.getSize());
        verify(ticketMapper, times(1)).selectPage(any(Page.class), any());
    }

    @Test
    void testGetTicketDetail() {
        when(ticketMapper.selectById(1L)).thenReturn(testTicket);

        Map<String, Object> result = ticketService.getTicketDetail(1L);

        assertNotNull(result);
        verify(ticketMapper, times(1)).selectById(1L);
    }

    @Test
    void testCreateTicket() {
        TicketCreateRequest request = new TicketCreateRequest();
        request.setTitle("新建工单");
        request.setContent("新建内容");
        request.setPriority(1);

        Map<String, Object> result = ticketService.createTicket(request);

        assertNotNull(result);
    }

    @Test
    void testUpdateTicket() {
        TicketUpdateRequest request = new TicketUpdateRequest();
        request.setTitle("更新后的标题");

        when(ticketMapper.selectById(1L)).thenReturn(testTicket);
        when(ticketMapper.updateById(any(Ticket.class))).thenReturn(1);

        boolean result = ticketService.updateTicket(1L, request);

        assertTrue(result);
        verify(ticketMapper, times(1)).updateById(any(Ticket.class));
    }

    @Test
    void testDeleteTicket() {
        when(ticketMapper.deleteById(1L)).thenReturn(1);

        boolean result = ticketService.deleteTicket(1L);

        assertTrue(result);
        verify(ticketMapper, times(1)).deleteById(1L);
    }

    @Test
    void testAssignTicket() {
        when(ticketMapper.selectById(1L)).thenReturn(testTicket);
        when(ticketMapper.updateById(any(Ticket.class))).thenReturn(1);

        boolean result = ticketService.assignTicket(1L, 2L, "测试分配");

        assertTrue(result);
        verify(ticketMapper, times(1)).updateById(any(Ticket.class));
    }

    @Test
    void testCloseTicket() {
        when(ticketMapper.selectById(1L)).thenReturn(testTicket);
        when(ticketMapper.updateById(any(Ticket.class))).thenReturn(1);

        boolean result = ticketService.closeTicket(1L, "测试关闭");

        assertTrue(result);
        verify(ticketMapper, times(1)).updateById(any(Ticket.class));
    }
}
