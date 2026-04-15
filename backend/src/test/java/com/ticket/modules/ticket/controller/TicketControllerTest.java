package com.ticket.modules.ticket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.common.constant.SystemConstants;
import com.ticket.modules.ticket.dto.TicketCreateRequest;
import com.ticket.modules.ticket.dto.TicketUpdateRequest;
import com.ticket.modules.ticket.entity.Ticket;
import com.ticket.modules.ticket.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
@DisplayName("TicketController Integration Tests")
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TicketService ticketService;

    private Ticket testTicket;
    private TicketCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testTicket = new Ticket();
        testTicket.setId(1L);
        testTicket.setTicketNo("TK202401010001");
        testTicket.setTitle("Test Ticket");
        testTicket.setStatus(SystemConstants.TicketStatus.NEW);

        createRequest = new TicketCreateRequest();
        createRequest.setTitle("New Ticket");
        createRequest.setContent("Test content");
        createRequest.setCustomerId(1L);
    }

    @Nested
    @DisplayName("GET /api/ticket/list")
    class GetListTests {

        @Test
        @WithMockUser
        @DisplayName("Should return paginated ticket list")
        void shouldReturnPaginatedTicketList() throws Exception {
            Page<Ticket> page = new Page<>(1, 10);
            page.setRecords(java.util.List.of(testTicket));
            page.setTotal(1);

            when(ticketService.pageTicket(any(Page.class), any(Ticket.class))).thenReturn(page);

            mockMvc.perform(get("/api/ticket/list")
                    .param("page", "1")
                    .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.records").isArray())
                    .andExpect(jsonPath("$.data.records[0].ticketNo").value("TK202401010001"));

            verify(ticketService).pageTicket(any(Page.class), any(Ticket.class));
        }
    }

    @Nested
    @DisplayName("GET /api/ticket/{id}")
    class GetDetailTests {

        @Test
        @WithMockUser
        @DisplayName("Should return ticket detail")
        void shouldReturnTicketDetail() throws Exception {
            Map<String, Object> detail = new HashMap<>();
            detail.put("ticket", testTicket);

            when(ticketService.getTicketDetail(1L)).thenReturn(detail);

            mockMvc.perform(get("/api/ticket/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.ticket.ticketNo").value("TK202401010001"));

            verify(ticketService).getTicketDetail(1L);
        }
    }

    @Nested
    @DisplayName("POST /api/ticket")
    class CreateTicketTests {

        @Test
        @WithMockUser
        @DisplayName("Should create ticket successfully")
        void shouldCreateTicketSuccessfully() throws Exception {
            Map<String, Object> result = new HashMap<>();
            result.put("id", 1L);
            result.put("ticketNo", "TK202401010001");

            when(ticketService.createTicket(any(TicketCreateRequest.class))).thenReturn(result);

            mockMvc.perform(post("/api/ticket")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.ticketNo").value("TK202401010001"));

            verify(ticketService).createTicket(any(TicketCreateRequest.class));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return validation error for invalid request")
        void shouldReturnValidationErrorForInvalidRequest() throws Exception {
            TicketCreateRequest invalidRequest = new TicketCreateRequest();

            mockMvc.perform(post("/api/ticket")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(ticketService, never()).createTicket(any());
        }
    }

    @Nested
    @DisplayName("PUT /api/ticket/{id}")
    class UpdateTicketTests {

        @Test
        @WithMockUser
        @DisplayName("Should update ticket successfully")
        void shouldUpdateTicketSuccessfully() throws Exception {
            TicketUpdateRequest updateRequest = new TicketUpdateRequest();
            updateRequest.setTitle("Updated Title");

            when(ticketService.updateTicket(eq(1L), any(TicketUpdateRequest.class))).thenReturn(true);

            mockMvc.perform(put("/api/ticket/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(ticketService).updateTicket(eq(1L), any(TicketUpdateRequest.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/ticket/{id}")
    class DeleteTicketTests {

        @Test
        @WithMockUser
        @DisplayName("Should delete ticket successfully")
        void shouldDeleteTicketSuccessfully() throws Exception {
            when(ticketService.deleteTicket(1L)).thenReturn(true);

            mockMvc.perform(delete("/api/ticket/1")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(ticketService).deleteTicket(1L);
        }
    }

    @Nested
    @DisplayName("POST /api/ticket/{id}/assign")
    class AssignTicketTests {

        @Test
        @WithMockUser
        @DisplayName("Should assign ticket successfully")
        void shouldAssignTicketSuccessfully() throws Exception {
            when(ticketService.assignTicket(eq(1L), eq(2L), any())).thenReturn(true);

            mockMvc.perform(post("/api/ticket/1/assign")
                    .with(csrf())
                    .param("handlerId", "2")
                    .param("remark", "Test assignment"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(ticketService).assignTicket(eq(1L), eq(2L), any());
        }
    }

    @Nested
    @DisplayName("POST /api/ticket/{id}/close")
    class CloseTicketTests {

        @Test
        @WithMockUser
        @DisplayName("Should close ticket successfully")
        void shouldCloseTicketSuccessfully() throws Exception {
            when(ticketService.closeTicket(eq(1L), any())).thenReturn(true);

            mockMvc.perform(post("/api/ticket/1/close")
                    .with(csrf())
                    .param("reason", "Closed for testing"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(ticketService).closeTicket(eq(1L), any());
        }
    }

    @Nested
    @DisplayName("GET /api/ticket/todo")
    class GetTodoTicketsTests {

        @Test
        @WithMockUser
        @DisplayName("Should return todo tickets")
        void shouldReturnTodoTickets() throws Exception {
            Page<Ticket> page = new Page<>(1, 10);
            page.setRecords(java.util.List.of(testTicket));

            when(ticketService.getTodoTickets(any(Page.class))).thenReturn(page);

            mockMvc.perform(get("/api/ticket/todo")
                    .param("page", "1")
                    .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.records").isArray());

            verify(ticketService).getTodoTickets(any(Page.class));
        }
    }

    @Nested
    @DisplayName("GET /api/ticket/dashboard")
    class GetDashboardTests {

        @Test
        @WithMockUser
        @DisplayName("Should return dashboard statistics")
        void shouldReturnDashboardStatistics() throws Exception {
            Map<String, Object> stats = new HashMap<>();
            stats.put("todayNew", 10);
            stats.put("processing", 5);

            when(ticketService.getDashboardStatistics()).thenReturn(stats);

            mockMvc.perform(get("/api/ticket/dashboard"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.todayNew").value(10));

            verify(ticketService).getDashboardStatistics();
        }
    }
}
