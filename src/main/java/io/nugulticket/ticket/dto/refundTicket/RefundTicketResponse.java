package io.nugulticket.ticket.dto.refundTicket;

import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.enums.TicketStatus;
import lombok.Getter;

@Getter
public class RefundTicketResponse {
    private Long ticketId;
    private TicketStatus ticketStatus;

    public RefundTicketResponse(Ticket ticket) {
        this.ticketId = ticket.getTicketId();
        this.ticketStatus = ticket.getStatus();
    }
}
