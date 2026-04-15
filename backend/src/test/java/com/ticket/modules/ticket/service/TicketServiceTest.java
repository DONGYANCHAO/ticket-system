package com.ticket.modules.ticket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.common.constant.SystemConstants;
import com.ticket.common.exception.BusinessException;
import com.ticket.common.util.SecurityUtils;
import com.ticket.modules.customer.entity.Customer;
import com.ticket.modules.customer.mapper.CustomerMapper;
import com.ticket.modules.system.entity.SystemUser;
import com.ticket.modules.system.mapper.SystemUserMapper;
import com.ticket.modules.ticket.dto.TicketCreateRequest;
import com.ticket.modules.ticket.dto.TicketUpdateRequest;
import com.ticket.modules.ticket.entity.Ticket;
import com.ticket.modules.ticket.mapper.TicketHistoryMapper;
import com.ticket.modules.ticket.mapper.TicketMapper;
import com.ticket.modules.ticket.mapper.TicketTagMapper;
import com.ticket.modules.ticket.service.impl.TicketServiceImpl;
import com.ticket.modules.ticket.service.TicketHistoryService;
import com.ticket.modules.ticket.service.TicketMessageService;
import com.ticket.modules.ticket.service.TicketTagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TicketService Unit Tests")
class TicketServiceTest {

    @Mock
    private TicketMapper ticketMapper;

    @Mock
    private TicketTagMapper ticketTagMapper;

    @Mock
    private TicketHistoryMapper ticketHistoryMapper;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private SystemUserMapper userMapper;

    @Mock
    private TicketTagService ticketTagService;

    @Mock
    private TicketHistoryService ticketHistoryService;

    @Mock
    private TicketMessageService ticketMessageService;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private Ticket testTicket;
    private Customer testCustomer;
    private SystemUser testUser;

    @BeforeEach
    void setUp() {
        testTicket = new Ticket();
        testTicket.setId(1L);
        testTicket.setTicketNo("TK202401010001");
        testTicket.setTitle("Test Ticket");
        testTicket.setStatus(SystemConstants.TicketStatus.NEW);
        testTicket.setCreatorId(1L);

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Customer");

        testUser = new SystemUser();
        testUser.setId(1L);
        testUser.setRealName("Test User");
        testUser.setRole(SystemConstants.Role.ADMIN);
    }

    @Nested
    @DisplayName("getTicketDetail Tests")
    class GetTicketDetailTests {

        @Test
        @DisplayName("Should return ticket detail when ticket exists")
        void shouldReturnTicketDetailWhenTicketExists() {
            when(ticketMapper.selectById(1L)).thenReturn(testTicket);

            Map<String, Object> result = ticketService.getTicketDetail(1L);

            assertNotNull(result);
            assertEquals(testTicket, result.get("ticket"));
            verify(ticketMapper).selectById(1L);
        }

        @Test
        @DisplayName("Should throw exception when ticket not found")
        void shouldThrowExceptionWhenTicketNotFound() {
            when(ticketMapper.selectById(anyLong())).thenReturn(null);

            assertThrows(BusinessException.class, () -> {
                ticketService.getTicketDetail(999L);
            });
        }
    }

    @Nested
    @DisplayName("createTicket Tests")
    class CreateTicketTests {

        @Test
        @DisplayName("Should create ticket successfully")
        void shouldCreateTicketSuccessfully() {
            TicketCreateRequest request = new TicketCreateRequest();
            request.setCustomerId(1L);
            request.setTitle("New Ticket");
            request.setContent("Test content");

            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

                when(customerMapper.selectById(1L)).thenReturn(testCustomer);
                when(ticketMapper.selectCount(any())).thenReturn(0L);
                when(ticketMapper.insert(any(Ticket.class))).thenAnswer(invocation -> {
                    Ticket ticket = invocation.getArgument(0);
                    ticket.setId(1L);
                    return 1;
                });

                Map<String, Object> result = ticketService.createTicket(request);

                assertNotNull(result);
                assertNotNull(result.get("ticketNo"));
                verify(ticketMapper).insert(any(Ticket.class));
                verify(ticketHistoryService).recordHistory(anyLong(), eq("CREATE"), anyString(), isNull(), isNull());
            }
        }

        @Test
        @DisplayName("Should throw exception when customer not found")
        void shouldThrowExceptionWhenCustomerNotFound() {
            TicketCreateRequest request = new TicketCreateRequest();
            request.setCustomerId(999L);
            request.setTitle("New Ticket");

            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
                when(customerMapper.selectById(999L)).thenReturn(null);

                assertThrows(BusinessException.class, () -> {
                    ticketService.createTicket(request);
                });
            }
        }
    }

    @Nested
    @DisplayName("updateTicket Tests")
    class UpdateTicketTests {

        @Test
        @DisplayName("Should update ticket successfully")
        void shouldUpdateTicketSuccessfully() {
            TicketUpdateRequest request = new TicketUpdateRequest();
            request.setTitle("Updated Title");

            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

                when(ticketMapper.selectById(1L)).thenReturn(testTicket);
                when(ticketMapper.updateById(any(Ticket.class))).thenReturn(1);

                boolean result = ticketService.updateTicket(1L, request);

                assertTrue(result);
                verify(ticketMapper).updateById(any(Ticket.class));
            }
        }

        @Test
        @DisplayName("Should throw exception when updating closed ticket")
        void shouldThrowExceptionWhenUpdatingClosedTicket() {
            testTicket.setStatus(SystemConstants.TicketStatus.CLOSED);
            TicketUpdateRequest request = new TicketUpdateRequest();
            request.setTitle("Updated Title");

            when(ticketMapper.selectById(1L)).thenReturn(testTicket);

            assertThrows(BusinessException.class, () -> {
                ticketService.updateTicket(1L, request);
            });
        }
    }

    @Nested
    @DisplayName("deleteTicket Tests")
    class DeleteTicketTests {

        @Test
        @DisplayName("Should delete ticket successfully when user is admin")
        void shouldDeleteTicketSuccessfullyWhenUserIsAdmin() {
            testTicket.setStatus(SystemConstants.TicketStatus.CLOSED);

            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(2L);

                when(ticketMapper.selectById(1L)).thenReturn(testTicket);
                when(userMapper.selectById(2L)).thenReturn(testUser);
                when(ticketMapper.deleteById(1L)).thenReturn(1);

                boolean result = ticketService.deleteTicket(1L);

                assertTrue(result);
                verify(ticketMapper).deleteById(1L);
            }
        }

        @Test
        @DisplayName("Should throw exception when deleting open ticket")
        void shouldThrowExceptionWhenDeletingOpenTicket() {
            testTicket.setStatus(SystemConstants.TicketStatus.PROCESSING);

            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

                when(ticketMapper.selectById(1L)).thenReturn(testTicket);
                when(userMapper.selectById(1L)).thenReturn(testUser);

                assertThrows(BusinessException.class, () -> {
                    ticketService.deleteTicket(1L);
                });
            }
        }
    }

    @Nested
    @DisplayName("assignTicket Tests")
    class AssignTicketTests {

        @Test
        @DisplayName("Should assign ticket successfully")
        void shouldAssignTicketSuccessfully() {
            SystemUser newHandler = new SystemUser();
            newHandler.setId(2L);
            newHandler.setRealName("New Handler");

            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

                when(ticketMapper.selectById(1L)).thenReturn(testTicket);
                when(userMapper.selectById(2L)).thenReturn(newHandler);
                when(ticketMapper.updateById(any(Ticket.class))).thenReturn(1);

                boolean result = ticketService.assignTicket(1L, 2L, "Test assignment");

                assertTrue(result);
                verify(ticketHistoryService).recordAssign(1L, null, 2L);
            }
        }

        @Test
        @DisplayName("Should throw exception when handler not found")
        void shouldThrowExceptionWhenHandlerNotFound() {
            when(ticketMapper.selectById(1L)).thenReturn(testTicket);
            when(userMapper.selectById(999L)).thenReturn(null);

            assertThrows(BusinessException.class, () -> {
                ticketService.assignTicket(1L, 999L, "Test");
            });
        }
    }

    @Nested
    @DisplayName("closeTicket Tests")
    class CloseTicketTests {

        @Test
        @DisplayName("Should close ticket successfully")
        void shouldCloseTicketSuccessfully() {
            when(ticketMapper.selectById(1L)).thenReturn(testTicket);
            when(ticketMapper.updateById(any(Ticket.class))).thenReturn(1);

            boolean result = ticketService.closeTicket(1L, "Closed for testing");

            assertTrue(result);
            assertEquals(SystemConstants.TicketStatus.CLOSED, testTicket.getStatus());
            assertNotNull(testTicket.getCloseTime());
            verify(ticketHistoryService).recordStatusChange(1L, SystemConstants.TicketStatus.CLOSED, SystemConstants.TicketStatus.CLOSED);
        }
    }

    @Nested
    @DisplayName("withdrawTicket Tests")
    class WithdrawTicketTests {

        @Test
        @DisplayName("Should withdraw ticket successfully")
        void shouldWithdrawTicketSuccessfully() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

                when(ticketMapper.selectById(1L)).thenReturn(testTicket);
                when(ticketMapper.updateById(any(Ticket.class))).thenReturn(1);

                boolean result = ticketService.withdrawTicket(1L);

                assertTrue(result);
                assertEquals(SystemConstants.TicketStatus.WITHDRAWN, testTicket.getStatus());
            }
        }

        @Test
        @DisplayName("Should throw exception when non-creator tries to withdraw")
        void shouldThrowExceptionWhenNonCreatorTriesToWithdraw() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(2L);

                when(ticketMapper.selectById(1L)).thenReturn(testTicket);

                assertThrows(BusinessException.class, () -> {
                    ticketService.withdrawTicket(1L);
                });
            }
        }
    }
}
