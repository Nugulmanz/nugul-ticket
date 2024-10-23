package io.nugulticket.ticket.dto.response;

import io.nugulticket.ticket.entity.Ticket;
import lombok.Getter;

import java.util.Random;

@Getter
public class TicketTransferResponse {
    Long ticketId;
    int price;

    public TicketTransferResponse(Ticket ticket) {
        this.ticketId = ticket.getTicketId();
        this.price = ticket.getSeat().getPrice();
    }
}
