package com.ticket.modules.ticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.modules.ticket.dto.TicketCreateRequest;
import com.ticket.modules.ticket.entity.Ticket;
import com.ticket.modules.ticket.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @Autowired
    private ObjectMapper objectMapper;

    private Ticket testTicket;

    @BeforeEach
    void setUp() {
        testTicket = new Ticket();
        testTicket.setId(1L);
        testTicket.setTitle("测试工单");
        testTicket.setContent("测试内容");
        testTicket.setStatus(1);
        testTicket.setPriority(2);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER", "ADMIN"})
    void testGetTicketList() throws Exception {
        mockMvc.perform(get("/tickets")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER", "ADMIN"})
    void testGetTicketDetail() throws Exception {
        Map<String, Object> detail = new HashMap<>();
        detail.put("id", 1L);
        detail.put("title", "测试工单");
        when(ticketService.getTicketDetail(1L)).thenReturn(detail);

        mockMvc.perform(get("/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER", "ADMIN"})
    void testCreateTicket() throws Exception {
        TicketCreateRequest request = new TicketCreateRequest();
        request.setTitle("新建工单");
        request.setContent("新建工单内容");
        request.setPriority(1);

        Map<String, Object> result = new HashMap<>();
        result.put("id", 1L);
        result.put("title", "新建工单");
        when(ticketService.createTicket(any(TicketCreateRequest.class))).thenReturn(result);

        mockMvc.perform(post("/tickets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER", "ADMIN"})
    void testDeleteTicket() throws Exception {
        when(ticketService.deleteTicket(1L)).thenReturn(true);

        mockMvc.perform(delete("/tickets/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
