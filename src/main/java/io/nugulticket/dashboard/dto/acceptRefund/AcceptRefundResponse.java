package io.nugulticket.dashboard.dto.acceptRefund;

import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.enums.TicketStatus;
import lombok.Getter;

@Getter
public class AcceptRefundResponse {
    private Long ticketId;
    private TicketStatus ticketStatus;

    public AcceptRefundResponse(Ticket ticket){
        this.ticketId = ticket.getTicketId();
        this.ticketStatus = ticket.getStatus();
    }
}
