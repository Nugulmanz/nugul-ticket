package io.nugulticket.ticket.service;

import io.nugulticket.ticket.config.TicketUtil;
import io.nugulticket.ticket.dto.response.GetMyTransferTicketsResponse;
import io.nugulticket.ticket.dto.response.TicketTransferApplyResponse;
import io.nugulticket.ticket.dto.response.TicketTransferCancleResponse;
import io.nugulticket.ticket.dto.response.TicketTransferResponse;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.entity.TicketTransfer;
import io.nugulticket.ticket.enums.TicketStatus;
import io.nugulticket.ticket.repository.TicketTransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class TicketTransferService {
    private final TicketUtil ticketUtil;
    private final TicketService ticketService;
    private final TicketTransferRepository ticketTransferRepository;

    /**
     * 양도 대기중인 Ticket에 양도 신청을 넣는 메서드
     * @param ticketId 양도 신청을 넣을 TicketId
     * @return 양도 결과가 담긴 Dto객체
     */
    @Transactional
    public TicketTransferApplyResponse applyTransfer(Long ticketId) {
        Long userId = 1L;
        Ticket ticket = ticketService.findTicketById(ticketId);

        // 해당 티켓 상태가 양도 가능한 상태인지 확인
        if(!ticketUtil.isAbleTicketApplyTransfer(ticket)) {
            throw new IllegalArgumentException();
        }

        // 해당 티켓의 상태를 변화 => 재양도시 체크하기 위함
        ticket.changeStatus(TicketStatus.TRANSFERRED);

        // 티켓 양도 결과를 DB에 저장
        TicketTransfer ticketTransfer = new TicketTransfer(ticket, userId);
        TicketTransfer savedTicketTransfer = ticketTransferRepository.save(ticketTransfer);

        return new TicketTransferApplyResponse(savedTicketTransfer);
    }

    /**
     * 양도 대기중인 Ticket을 취소하는 메서드
     * @param ticketId 양도 취소할 TicketId
     * @return 양도 취소한 Id가 담긴 Dto객체
     */
    @Transactional
    public TicketTransferCancleResponse cancelTransfer(Long ticketId) {
        Long userId = 1L;
        Ticket ticket = ticketService.findTicketById(ticketId);

        // 해당 티켓이 양도 가능 상태인지 확인
        if (!ticketUtil.isAbleTicketCancelTransfer(ticket, userId)) {
            throw new IllegalArgumentException();
        }

        // 티켓을 양도 대기 -> 예약 상태로 변화
        ticket.changeStatus(TicketStatus.RESERVED);

        return new TicketTransferCancleResponse(ticket.getTicketId());
    }

    /**
     * 해당 티켓 Id를 가진 티켓을 양도할 메서드
     * @param ticketId 양도할Ticket Id
     * @return 양도 내용이 담긴 Dto 객체
     */
    @Transactional
    public TicketTransferResponse ticketTransfer(Long ticketId) {
        Long userId = 1L;
        Ticket ticket = ticketService.findTicketById(ticketId);

        // 해당 티켓이 양도 가능 상태인지 확인
        if (!ticketUtil.isAbleTicketTransfer(ticket, userId)) {
            throw new IllegalArgumentException();
        }

        // 티켓을 양도 대기 -> 예약 상태로 변화
        ticket.changeStatus(TicketStatus.WAITTRANSFER);

        return new TicketTransferResponse(ticket);
    }

    /**
     * 내가 양도하거나, 양도 받은 Ticket 이력을 조회하는 메서드
     * @return 내가 양도하거나, 양도 받은 Ticket 이력이 담긴 Dto객체
     */
    public GetMyTransferTicketsResponse getMyTransferTicket() {
        Long userId = 1L;
        List<Ticket> tickets = ticketService.findAllTicketByUserAndStatus(TicketStatus.WAITTRANSFER, userId);

        return new GetMyTransferTicketsResponse(tickets);
    }

}
