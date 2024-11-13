package io.nugulticket.ticket.dto.response;

import io.nugulticket.ticket.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TicketTransferResponse {
    Long ticketId;
    int price;

    public static TicketTransferResponse of(Ticket ticket) {
        return new TicketTransferResponse(ticket.getTicketId(), ticket.getSeat().getPrice());
    }
}
