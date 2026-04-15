package com.ticket.modules.ticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.modules.ticket.dto.TicketCreateRequest;
import com.ticket.modules.ticket.dto.TicketUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 工单控制器集成测试
 * 使用 @SpringBootTest 和 MockMvc 进行 API 测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("工单控制器集成测试")
class TicketControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private TicketCreateRequest createRequest;
    private TicketUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        createRequest = new TicketCreateRequest();
        createRequest.setTitle("测试工单标题");
        createRequest.setContent("测试工单内容");
        createRequest.setPriority(1);
        createRequest.setCustomerId(1L);

        updateRequest = new TicketUpdateRequest();
        updateRequest.setTitle("更新后的标题");
        updateRequest.setContent("更新后的内容");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("创建工单 - 成功")
    void createTicket_Success() throws Exception {
        // when & then
        mockMvc.perform(post("/api/ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("创建工单 - 参数验证失败")
    void createTicket_ValidationFailed() throws Exception {
        // given
        createRequest.setTitle(""); // 空标题

        // when & then
        mockMvc.perform(post("/api/ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("获取工单详情 - 成功")
    void getTicketDetail_Success() throws Exception {
        // given - 先创建一个工单
        MvcResult createResult = mockMvc.perform(post("/api/ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // when & then
        mockMvc.perform(get("/api/ticket/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("获取工单详情 - 不存在")
    void getTicketDetail_NotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/api/ticket/{id}", 99999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("分页查询工单 - 成功")
    void pageTickets_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/api/ticket/page")
                        .param("current", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("更新工单 - 成功")
    void updateTicket_Success() throws Exception {
        // given - 先创建一个工单
        mockMvc.perform(post("/api/ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        // when & then
        mockMvc.perform(put("/api/ticket/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("删除工单 - 成功")
    void deleteTicket_Success() throws Exception {
        // given - 先创建一个工单
        mockMvc.perform(post("/api/ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        // when & then
        mockMvc.perform(delete("/api/ticket/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("分配工单 - 成功")
    void assignTicket_Success() throws Exception {
        // given - 先创建一个工单
        mockMvc.perform(post("/api/ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        // when & then
        mockMvc.perform(post("/api/ticket/{id}/assign", 1)
                        .param("handlerId", "2")
                        .param("remark", "分配给工程师处理"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("关闭工单 - 成功")
    void closeTicket_Success() throws Exception {
        // given - 先创建一个工单
        mockMvc.perform(post("/api/ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        // when & then
        mockMvc.perform(post("/api/ticket/{id}/close", 1)
                        .param("reason", "客户要求关闭"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("解决工单 - 成功")
    void solveTicket_Success() throws Exception {
        // given - 先创建一个工单
        mockMvc.perform(post("/api/ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        // when & then
        mockMvc.perform(post("/api/ticket/{id}/solve", 1)
                        .param("remark", "问题已解决"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("催单 - 成功")
    void remindTicket_Success() throws Exception {
        // given - 先创建一个工单
        mockMvc.perform(post("/api/ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        // when & then
        mockMvc.perform(post("/api/ticket/{id}/remind", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("获取我的待办工单")
    void getTodoTickets_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/api/ticket/todo")
                        .param("current", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("获取我创建的工单")
    void getMyCreatedTickets_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/api/ticket/my-created")
                        .param("current", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    @DisplayName("未认证访问 - 返回 401")
    void unauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/ticket/page"))
                .andExpect(status().isUnauthorized());
    }
}
