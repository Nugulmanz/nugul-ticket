package io.nugulticket.ticket.service;

import io.nugulticket.common.AuthUser;
import io.nugulticket.event.entity.Event;
import io.nugulticket.event.service.EventService;
import io.nugulticket.seat.entity.Seat;
import io.nugulticket.seat.service.SeatService;
import io.nugulticket.ticket.dto.createTicket.CreateTicketRequest;
import io.nugulticket.ticket.dto.createTicket.CreateTicketResponse;
import io.nugulticket.ticket.dto.refundTicket.RefundTicketResponse;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.enums.TicketStatus;
import io.nugulticket.ticket.repository.TicketRepository;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final SeatService seatService;
    private final EventService eventService;
    private final UserService userService;

    /**
     * 해당 Id를 가진 티켓을 반환하는 메서드
     * @param id 조회할 Id
     * @return 해당 Id를 가진 티켓 / 없을 경우 NotFoundException 발생
     */
    public Ticket getTicket(Long id) {
        return ticketRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    /**
     * 해당 Id를 가진 티켓을 반환하는 메서드
     * @param id 조회할 Id
     * @return 해당 Id를 가진 티켓 / 없을 경우 NotFoundException 발생
     */
    public Ticket getTicketJoinFetchSeat(Long id) {
        return ticketRepository.findByIdJoinFetchSeat(id).orElseThrow(EntityNotFoundException::new);
    }

    /**
     * 해당 유저가 구매한 Ticket중에 status가 동일한 상태인 Ticket에 대해
     * Event, Seat를 fetch join하여 반환하는 메서드
     * @param status 티켓 상태
     * @param userId 구매자 Id
     * @return 해당 구매자가 구매한 티켓 중 해당 상태인 티켓 리스트
     */
    public List<Ticket> getAllTicketJoinFetchEventSeat(TicketStatus status, Long userId) {
        return ticketRepository.findAllEqualParamIdJoinFetchSeatAndEvent(status.name(), userId);
    }
    public List<Ticket> findAllTicketByUserAndStatus(TicketStatus status, Long userId) {
        return ticketRepository.findAllByStatusAndUser_Id(status, userId);
        }

    @Transactional
    public CreateTicketResponse createTicket(CreateTicketRequest reqDto, Long userId) {
        Seat seat = seatService.findSeatById(reqDto.getSeatId()); // 락 필요
        if(seat.isReserved()){
            throw new IllegalArgumentException("이미 예약된 좌석입니다."); // res에 메세지 보이지 않음
        }
        // 결제 기능 구현 필요

        Long eventId = seat.getEventTime().getEvent().getEventId(); // n+1 문제 있을 듯
        Event event = eventService.getEventFromId(eventId);
        String qrCode = createQRCode();
        Ticket ticket = new Ticket();
        User user = userService.getUser(userId);
        ticket.createTicket(event, seat, user, qrCode);
        seat.seatReserved();
        ticketRepository.save(ticket);

        CreateTicketResponse resDto = new CreateTicketResponse(seat, event, ticket, userId);
        return resDto;
    }

    public RefundTicketResponse refundTicket(Long ticketId, AuthUser authUser) {
        Ticket ticket = ticketRepository.findByUser_IdAndTicketId(authUser.getId(), ticketId)
                .orElseThrow(IllegalArgumentException::new);
        ticket.requestCancel();
        ticketRepository.save(ticket);

        return new RefundTicketResponse(ticket);
    }

    public Ticket getRefundTicket(long userId, long eventId, long ticketId) {
        Ticket ticket = ticketRepository
                .findByUser_IdAndTicketIdAndEvent_EventId( userId,ticketId, eventId)
                .orElseThrow(IllegalArgumentException::new);
        return ticket;
    }

    private String createQRCode(){
        return UUID.randomUUID().toString();
    }
}
