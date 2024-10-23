package io.nugulticket.ticket.repository;

import io.nugulticket.ticket.entity.TicketTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTransferRepository extends JpaRepository<TicketTransfer, Long> {
}
