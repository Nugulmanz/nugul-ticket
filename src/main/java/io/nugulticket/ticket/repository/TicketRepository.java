package io.nugulticket.ticket.repository;

import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByStatusAndUser_Id(TicketStatus status, Long buyerId);

    Optional<Ticket> findByUser_IdAndTicketId(Long userId, Long ticketId);
}
