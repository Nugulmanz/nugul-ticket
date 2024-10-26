package io.nugulticket.ticket.repository;

import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByStatusAndUser_Id(TicketStatus status, Long buyerId);

    Optional<Ticket> findByUser_IdAndTicketId(Long userId, Long ticketId);

    Optional<Ticket> findByUser_IdAndTicketIdAndEvent_EventId(Long userId, Long ticketId, Long eventId);
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
            "join fetch t.user u " +
//            "join fetch s.eventTime et " +
            "where t.status = :status and t.user.id = :buyerId")
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


    /**
     * 공연제목 키워드, 공연날짜로 검색하고 그 공연의 양도 가능한 티켓을 검색하는 메서드
     * @param keyword
     * @param eventDate
     * @param pageable
     * @return Ticket 페이지 형태로 리턴
     */
    @Query("select t from Ticket t " +
            "join fetch t.event e " +
            "join fetch t.seat s " +
            "where t.status = 'WAITTRANSFER' " +
            "and (:keyword IS NULL OR e.title LIKE CONCAT('%', :keyword, '%')) " +
            "and (:eventDate IS NULL OR :eventDate BETWEEN e.startDate AND e.endDate) " +
            "order by t.ticketId")
    Page<Ticket> findByKeywords(String keyword, LocalDate eventDate, Pageable pageable);
}
