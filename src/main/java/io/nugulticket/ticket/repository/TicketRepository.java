package io.nugulticket.ticket.repository;

import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByStatusAndBuyerId(TicketStatus status, Long buyerId);
}
