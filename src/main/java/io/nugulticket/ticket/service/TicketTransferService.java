package io.nugulticket.ticket.service;

import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.common.utils.payment.GenerateOrderIdUtil;
import io.nugulticket.lock.RedisDistributedLock;
import io.nugulticket.ticket.config.TicketUtil;
import io.nugulticket.ticket.dto.response.MyTransferTicketsResponse;
import io.nugulticket.ticket.dto.response.TicketNeedPaymentResponse;
import io.nugulticket.ticket.dto.response.TicketTransferCancelResponse;
import io.nugulticket.ticket.dto.response.TicketTransferResponse;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.entity.TicketTransfer;
import io.nugulticket.ticket.enums.TicketStatus;
import io.nugulticket.ticket.repository.TicketTransferRepository;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketTransferService {
    private final TicketUtil ticketUtil;
    private final TicketService ticketService;
    private final TicketTransferRepository ticketTransferRepository;
    private final GenerateOrderIdUtil generateOrderIdUtil;
    private final UserService userService;

    /**
     * 양도 대기중인 Ticket에 양도 신청을 넣는 메서드
     * @param ticketId 양도 신청을 넣을 TicketId
     * @return 양도 결과가 담긴 Dto객체
     */
    @RedisDistributedLock(key = "applyTransferBeforePayment")
    public TicketNeedPaymentResponse applyTransferBeforePayment(AuthUser users, Long ticketId) {
        User user = userService.getUser(users.getId());
        Ticket ticket = ticketService.getTicket(ticketId);

        // 해당 티켓 상태가 양도 가능한 상태인지 확인
        if(!ticketUtil.isAbleTicketApplyTransfer(ticket, user.getId())) {
            throw new ApiException(ErrorStatus._CANT_TRANSFER_STATE);
        }

        ticket.changeStatus(TicketStatus.WAITING_RESERVED);

        return TicketNeedPaymentResponse.of(ticket, users,"transfer", generateOrderIdUtil.generateOrderId());
    }

    /**
     * 결제 후 티켓 소유권을 이전하는 메서드
     * @param ticketId 소유권을 이전할 티켓 메서드
     * @param userId 소유권을 이전 받을 유저 Id
     * @return 해당 소유권을 이전한 티켓 정보가 담긴 Response 객체
     */
    @Transactional
    public TicketTransferResponse applyTransferAfterPayment(Long ticketId, Long userId) {
        Ticket ticket = ticketService.getTicket(ticketId);

        ticket.changeStatus(TicketStatus.TRANSFERRED);

        TicketTransfer ticketTransfer = new TicketTransfer(ticket, userId);
        TicketTransfer savedTicketTransfer = ticketTransferRepository.save(ticketTransfer);

        return TicketTransferResponse.of(ticket);
    }

    /**
     * 결제 실패 후 티켓 상태를 롤백하는 메서드
     * @param ticketId 소유권을 롤백할 티켓 Id
     * @return 상태를 롤백 시킨 티켓 정보가 담긴 Response 객체
     */
    @Transactional
    public TicketTransferResponse cancelTransferAfterPayment(Long ticketId) {
        Ticket ticket = ticketService.getTicket(ticketId);

        ticket.changeStatus(TicketStatus.WAITTRANSFER);

        return TicketTransferResponse.of(ticket);
    }

    /**
     * 양도 대기중인 Ticket을 취소하는 메서드
     * @param ticketId 양도 취소할 TicketId
     * @return 양도 취소한 Id가 담긴 Dto객체
     */
    @Transactional
    public TicketTransferCancelResponse cancelTransfer(AuthUser user, Long ticketId) {
        Ticket ticket = ticketService.getTicket(ticketId);

        // 해당 티켓이 양도 가능 상태인지 확인
        if (!ticketUtil.isAbleTicketCancelTransfer(ticket, user.getId())) {
            throw new ApiException(ErrorStatus._CANT_TRANSFER_STATE);
        }

        // 티켓을 양도 대기 -> 예약 상태로 변화
        ticket.changeStatus(TicketStatus.RESERVED);

        return TicketTransferCancelResponse.of(ticket.getTicketId());
    }

    /**
     * 해당 티켓 Id를 가진 티켓을 양도할 메서드
     * @param ticketId 양도할Ticket Id
     * @return 양도 내용이 담긴 Dto 객체
     */
    @Transactional
    public TicketTransferResponse ticketTransfer(AuthUser user, Long ticketId) {
        Ticket ticket = ticketService.getTicketJoinFetchSeat(ticketId);

        // 해당 티켓이 양도 가능 상태인지 확인
        if (!ticketUtil.isAbleTicketTransfer(ticket, user.getId())) {
            throw new ApiException(ErrorStatus._CANT_TRANSFER_STATE);
        }

        // 티켓을 양도 대기 -> 예약 상태로 변화
        ticket.changeStatus(TicketStatus.WAITTRANSFER);

        return TicketTransferResponse.of(ticket);
    }

    /**
     * 내가 양도하거나, 양도 받은 Ticket 이력을 조회하는 메서드
     * @return 내가 양도하거나, 양도 받은 Ticket 이력이 담긴 Dto객체
     */
    @Transactional(readOnly = true)
    public MyTransferTicketsResponse getMyTransferTicket(AuthUser user) {
        List<Ticket> tickets = ticketService.getAllTicketJoinFetchEventSeat(TicketStatus.WAITTRANSFER, user.getId());

        return MyTransferTicketsResponse.of(tickets);
    }
}
