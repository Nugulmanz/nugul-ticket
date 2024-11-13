package io.nugulticket.tickettransfer;

import io.nugulticket.common.AuthUser;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.common.utils.payment.GenerateOrderIdUtil;
import io.nugulticket.event.service.EventService;
import io.nugulticket.seat.service.SeatService;
import io.nugulticket.ticket.config.TicketUtil;
import io.nugulticket.ticket.dto.response.MyTransferTicketsResponse;
import io.nugulticket.ticket.dto.response.TicketNeedPaymentResponse;
import io.nugulticket.ticket.dto.response.TicketTransferCancelResponse;
import io.nugulticket.ticket.dto.response.TicketTransferResponse;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.entity.TicketTransfer;
import io.nugulticket.ticket.repository.TicketRepository;
import io.nugulticket.ticket.repository.TicketTransferRepository;
import io.nugulticket.ticket.service.TicketService;
import io.nugulticket.ticket.service.TicketTransferService;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.repository.UserRepository;
import io.nugulticket.user.service.UserService;
import io.nugulticket.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TicketTransferServiceTest {

    @InjectMocks
    private TicketTransferService ticketTransferService;

    @Mock
    private TicketUtil ticketUtil;

    @Mock
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private SeatService seatService;

    @InjectMocks
    private EventService eventService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private GenerateOrderIdUtil generateOrderIdUtil;

    @Mock
    private TicketTransferRepository ticketTransferRepository;

    @Test
    public void 티켓이_양도가능한_상태가_아닐경우_UAE에러를_반환한다() {
        long userId = 1L;
        Long ticketId = 1L;
        User user = TestUtil.getUser(userId);
        Ticket ticket = TestUtil.getTicket(ticketId);
        AuthUser authUser = new AuthUser(user.getId(), user.getAddress(), user.getUserRole());

        given(userService.getUser(anyLong())).willReturn(user);
        given(ticketService.getTicket(anyLong())).willReturn(ticket);

        ApiException exception = assertThrows(ApiException.class,
                () -> ticketTransferService.applyTransferBeforePayment(authUser, ticketId));
    }

    @Test
    public void 티켓_양도에_성공한다() {
        long userId = 1L;
        Long ticketId = 1L;
        User user = TestUtil.getUser(userId);
        Ticket ticket = TestUtil.getTicket(ticketId);
        AuthUser authUser = new AuthUser(user.getId(), user.getAddress(), user.getUserRole());

        given(userService.getUser(anyLong())).willReturn(user);
        given(ticketService.getTicket(anyLong())).willReturn(ticket);
        given(ticketUtil.isAbleTicketApplyTransfer(any(), any())).willReturn(true);

        TicketNeedPaymentResponse response = ticketTransferService.applyTransferBeforePayment(authUser, ticketId);

        assertNotNull(response);
    }

    @Test
    public void 티켓_양도취소가_불가능할경우_APE를_반환한다() {
        long userId = 1L;
        Long ticketId = 1L;
        User user = TestUtil.getUser(userId);
        Ticket ticket = TestUtil.getTicket(ticketId);
        AuthUser authUser = new AuthUser(user.getId(), user.getAddress(), user.getUserRole());

        ApiException exception = assertThrows(ApiException.class,
                () -> ticketTransferService.cancelTransfer(authUser, ticketId));
    }

    @Test
    public void 티켓_양도취소에_성공한다() {
        long userId = 1L;
        Long ticketId = 1L;
        User user = TestUtil.getUser(userId);
        Ticket ticket = TestUtil.getTicket(ticketId);
        AuthUser authUser = new AuthUser(user.getId(), user.getAddress(), user.getUserRole());

        given(ticketService.getTicket(anyLong())).willReturn(ticket);
        given(ticketUtil.isAbleTicketCancelTransfer(any(), any())).willReturn(true);

        TicketTransferCancelResponse ticketTransferCancelResponse = ticketTransferService.cancelTransfer(authUser, ticketId);

        assertNotNull(ticketTransferCancelResponse);
    }

    @Test
    public void 티켓_양도가_불가능한_상황인경우_APE를_반환한다() {
        long userId = 1L;
        Long ticketId = 1L;
        User user = TestUtil.getUser(userId);
        Ticket ticket = TestUtil.getTicket(ticketId);
        AuthUser authUser = new AuthUser(user.getId(), user.getAddress(), user.getUserRole());

        given(ticketService.getTicketJoinFetchSeat(anyLong())).willReturn(ticket);
        given(ticketUtil.isAbleTicketTransfer(any(), any())).willReturn(false);

        ApiException exception = assertThrows(ApiException.class,
                () -> ticketTransferService.ticketTransfer(authUser, ticketId));
    }

    @Test
    public void 티켓_양도신청에_성공한다() {
        long userId = 1L;
        Long ticketId = 1L;
        User user = TestUtil.getUser(userId);
        Ticket ticket = TestUtil.getTicket(ticketId);
        AuthUser authUser = new AuthUser(user.getId(), user.getAddress(), user.getUserRole());

        given(ticketService.getTicketJoinFetchSeat(anyLong())).willReturn(ticket);
        given(ticketUtil.isAbleTicketTransfer(any(), any())).willReturn(true);

        TicketTransferResponse response = ticketTransferService.ticketTransfer(authUser, userId);
        assertNotNull(response);
    }

    @Test
    public void 티켓_결제_후_상태_변경에_성공한다() {
        long userId = 1L;
        Long ticketId = 1L;
        Long ticketTransferId = 1L;
        Ticket ticket = TestUtil.getTicket(ticketId);
        TicketTransfer ticketTransfer = TestUtil.getTicketTransfer(ticketTransferId, userId, ticket);

        given(ticketService.getTicket(anyLong())).willReturn(ticket);
        given(ticketTransferRepository.save(any())).willReturn(ticketTransfer);

        TicketTransferResponse response = ticketTransferService.applyTransferAfterPayment(ticketId, userId);

        assertNotNull(response);
        assertEquals(ticketId, response.getTicketId());
    }

    @Test
    public void 티켓_결제_실패_후_상태_변경에_성공한다() {
        long userId = 1L;
        Long ticketId = 1L;
        Ticket ticket = TestUtil.getTicket(ticketId);

        given(ticketService.getTicket(anyLong())).willReturn(ticket);

        TicketTransferResponse response = ticketTransferService.cancelTransferAfterPayment(ticketId);

        assertNotNull(response);
        assertEquals(ticketId, response.getTicketId());
    }

    @Test
    public void 내_양도_이력을_조회한다() {
        long userId = 1L;
        Long ticketId = 1L;
        User user = TestUtil.getUser(userId);
        Ticket ticket = TestUtil.getTicket(ticketId);
        AuthUser authUser = new AuthUser(user.getId(), user.getAddress(), user.getUserRole());

        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);

        given(ticketService.getAllTicketJoinFetchEventSeat(any(), any())).willReturn(tickets);

        MyTransferTicketsResponse response = ticketTransferService.getMyTransferTicket(authUser);

        assertNotNull(response);
    }
}
