package io.nugulticket.ticket.repository;

import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByStatusAndUser_Id(TicketStatus status, Long buyerId);

    Optional<Ticket> findByUser_IdAndTicketId(Long userId, Long ticketId);

    Optional<Ticket> findByUser_IdAndTicketIdAndEvent_EventId(Long userId, Long ticketId, Long eventId);
    List<Ticket> findAllByStatusAndBuyerId(TicketStatus status, Long buyerId);
    /**
     * 해당 유저( buyerId )가 구매한 Ticket중에 status가 동일한 상태인 Ticket에 대해
     * Event, Seat를 fetch join하여 반환하는 쿼리
     * @param status 티켓 상태
     * @param buyerId 구매자 Id
     * @return 해당 구매자가 구매한 티켓 중 해당 상태인 티켓 리스트
     */
    @Query("select t from Ticket t " +
            "join fetch t.event e " +
            "join fetch t.seat s " +
            "where t.status = :status and t.buyerId = :buyerId")
    List<Ticket> findAllEqualParamIdJoinFetchSeatAndEvent(String status, Long buyerId);

    /**
     * 해당 ID를 가진 Ticket에 대해 Event를 fetch join하여 반환하는 쿼리
     * @param ticketId 티켓 상태
     * @return 해당 ID를 가진 Ticket
     */
    @Query("select t from Ticket t " +
            "join fetch t.event e " +
            "where t.ticketId = :ticketId")
    Optional<Ticket> findByIdJoinFetchEvent(Long ticketId);

    /**
     * 해당 ID를 가진 Ticket에 대해 Event를 fetch join하여 반환하는 쿼리
     * @param ticketId 티켓 상태
     * @return 해당 ID를 가진 Ticket
     */
    @Query("select t from Ticket t " +
            "join fetch t.seat s " +
            "where t.ticketId = :ticketId")
    Optional<Ticket> findByIdJoinFetchSeat(Long ticketId);
}
