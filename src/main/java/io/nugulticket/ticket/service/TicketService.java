package io.nugulticket.ticket.service;

import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.common.utils.payment.GenerateOrderIdUtil;
import io.nugulticket.event.entity.Event;
import io.nugulticket.event.service.EventService;
import io.nugulticket.lock.RedisDistributedLock;
import io.nugulticket.seat.entity.Seat;
import io.nugulticket.seat.service.SeatService;
import io.nugulticket.ticket.dto.createTicket.CreateTicketRequest;
import io.nugulticket.ticket.dto.refundTicket.RefundTicketResponse;
import io.nugulticket.ticket.dto.response.TicketNeedPaymentResponse;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.enums.TicketStatus;
import io.nugulticket.ticket.repository.TicketRepository;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final SeatService seatService;
    private final EventService eventService;
    private final UserService userService;
    private final GenerateOrderIdUtil generateOrderIdUtil;

    /**
     * 해당 Id를 가진 티켓을 반환하는 메서드
     *
     * @param id 조회할 Id
     * @return 해당 Id를 가진 티켓 / 없을 경우 NotFoundException 발생
     */
    @Transactional(readOnly = true)
    public Ticket getTicket(Long id) {
        return ticketRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    /**
     * 해당 Id를 가진 티켓을 반환하는 메서드
     *
     * @param id 조회할 Id
     * @return 해당 Id를 가진 티켓 / 없을 경우 NotFoundException 발생
     */
    @Transactional(readOnly = true)
    public Ticket getTicketJoinFetchSeat(Long id) {
        return ticketRepository.findByIdJoinFetchSeat(id).orElseThrow(EntityNotFoundException::new);
    }

    /**
     * 해당 유저가 구매한 Ticket중에 status가 동일한 상태인 Ticket에 대해
     * Event, Seat를 fetch join하여 반환하는 메서드
     *
     * @param status 티켓 상태
     * @param userId 구매자 Id
     * @return 해당 구매자가 구매한 티켓 중 해당 상태인 티켓 리스트
     */
    @Transactional(readOnly = true)
    public List<Ticket> getAllTicketJoinFetchEventSeat(TicketStatus status, Long userId) {
        return ticketRepository.findAllEqualParamIdJoinFetchSeatAndEvent(status, userId);
    }

    @Transactional(readOnly = true)
    public Ticket getTicketJoinFetchSeatAndEventTime(Long ticketId) {
        return ticketRepository.findByIdJoinFetchSeatAndEventTime(ticketId);
    }

    /**
     * 티켓을 예매하는 메서드 ( 상태는 결제 대기 상태로 생성 )
     *
     * @param reqDto   티켓 예매에 필요한 정보가 담긴 Request 객체
     * @param authUser 현재 로그인 중인 유저 정보
     * @return 결제에 사용될 정보가 담긴 Response 객체
     */
    @RedisDistributedLock(key = "createTicket")
    public TicketNeedPaymentResponse createTicket(CreateTicketRequest reqDto, AuthUser authUser) {
        Seat seat = seatService.findSeatById(reqDto.getSeatId()); // 락 필요
        if (seat.isReserved()) {
            throw new ApiException(ErrorStatus._ALREADY_RESERVED);
        }

        // 티켓 생성 및 저장
        Long eventId = seat.getEventTime().getEvent().getEventId(); // n+1 문제 있을 듯
        Event event = eventService.getEventFromId(eventId);
        String qrCode = createQRCode();
        Ticket ticket = new Ticket();
        User user = userService.getUser(authUser.getId());
        ticket.createTicket(event, seat, user, qrCode);
        seat.seatReserved();
        Ticket t = ticketRepository.save(ticket);
//        Ticket t = creatingTicket(authUser, seat);

//        CreateTicketResponse resDto123 = new CreateTicketResponse(seat, event, ticket, authUser.getId());
        return TicketNeedPaymentResponse.of(t, authUser, "ticket", generateOrderIdUtil.generateOrderId());
    }

//    @Transactional
//    public Ticket creatingTicket(AuthUser authUser, Seat seat) {
//        // 티켓 생성 및 저장
//        Long eventId = seat.getEventTime().getEvent().getEventId(); // n+1 문제 있을 듯
//        Event event = eventService.getEventFromId(eventId);
//        String qrCode = createQRCode();
//        Ticket ticket = new Ticket();
//        User user = userService.getUser(authUser.getId());
//        ticket.createTicket(event, seat, user, qrCode);
//        seat.seatReserved();
//        Ticket t = ticketRepository.save(ticket);
//        return t;
//    }

    /**
     * id에 대당하는 티켓 상태를 예매 완료 상태로 바꾸는 메서드
     *
     * @param id 티켓 상태를 바꿀 티켓 Id
     */
    @Transactional
    public void reserveTicket(Long id) {
        Ticket ticket = getTicketJoinFetchSeat(id);
        ticket.changeStatus(TicketStatus.RESERVED);
    }

    /**
     * id에 대당하는 티켓 상태를 예매 취소 상태로 바꾸는 메서드
     *
     * @param id 티켓 상태를 바꿀 티켓 Id
     */
    @Transactional
    public void cancelTicket(Long id) {
        Ticket ticket = getTicketJoinFetchSeat(id);
        ticket.changeStatus(TicketStatus.CANCELLED);
    }

    /**
     * 로그인 중인 유저가 예매한 해당 티켓을 환불 하는 메서드
     *
     * @param ticketId 해당 유저가 예매한 티켓 Id
     * @param authUser 로그인한 유저
     * @return 환불된 티켓 정보가 담긴 Response 객체
     */
    @Transactional
    public RefundTicketResponse refundTicket(Long ticketId, AuthUser authUser) {
        Ticket ticket = ticketRepository.findByUser_IdAndTicketId(authUser.getId(), ticketId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_TICKET));
        ticket.requestCancel();

        try {
            ticketRepository.save(ticket);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ApiException(ErrorStatus.VERSION_CONFLICT);
        }

        return new RefundTicketResponse(ticket);
    }

    /**
     * 해당 유저가 예매 했었던 특정 공연에 해당하는 특정 티켓 Id
     *
     * @param userId   조회할 유저 Id
     * @param eventId  조회할 공연 Id
     * @param ticketId 조회할 티켓 Id
     * @return 해당 유저가 예매 했었던 특정 공연에 해당하는 특정 티켓 Id
     */
    @Transactional(readOnly = true)
    public Ticket getRefundTicket(long userId, long eventId, long ticketId) {
        Ticket ticket = ticketRepository
                .findByUser_IdAndTicketIdAndEvent_EventId(userId, ticketId, eventId)
                .orElseThrow(IllegalArgumentException::new);
        return ticket;
    }

    /**
     * 무작위 QRCode를 반환하는 메서드
     *
     * @return 문자열로 변환한 UUID 정보
     */
    private String createQRCode() {
        return UUID.randomUUID().toString();
    }

    /**
     * 키워드 / 공연 일자 / 페이징 정보를 토대로 Ticket 정보를 반환하는 메서드
     *
     * @param keyword   조회할 공연 키워드
     * @param eventDate 조회할 공연 일자
     * @param pageable  Page, Size가 담긴 페이징 정보
     * @return 해당 정보들을 포함하고 있는 페이징된 Ticket 정보들
     */
    @Transactional(readOnly = true)
    public Page<Ticket> getTicketsFromKeywords(String keyword, LocalDate eventDate, Pageable pageable) {
        return ticketRepository.findByKeywords(keyword, eventDate, pageable);
    }

    /**
     * 특정 Ticket ID를 기반으로 QR 코드 정보를 조회하는 메서드
     *
     * @param ticketId 조회할 티켓의 고유 ID
     * @return 해당 티켓에 저장된 QR 코드 문자열 (QR 코드 정보가 포함된 문자열)
     */
    public String getQRCodeByTicketId(Long ticketId) {
        // 티켓 조회
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_TICKET));

        // QR 코드 확인
        if (ticket.getQrCode() == null || ticket.getQrCode().isEmpty()) {
            throw new ApiException(ErrorStatus.QR_CODE_NOT_FOUND);
        }

        return ticket.getQrCode(); // 정상 반환
    }


}
