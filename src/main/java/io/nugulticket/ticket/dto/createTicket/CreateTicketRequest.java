package io.nugulticket.ticket.dto.createTicket;

import lombok.Getter;

@Getter
public class CreateTicketRequest {
    private Long eventId;
    private Long seatId;
}
