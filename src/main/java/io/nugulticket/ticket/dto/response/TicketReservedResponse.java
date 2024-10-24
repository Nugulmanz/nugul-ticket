package io.nugulticket.ticket.dto.response;

import io.nugulticket.ticket.entity.Ticket;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class TicketReservedResponse {
    private Long ticketId;
    private String eventTitle;
    private LocalDateTime purchaseDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String qrCode;

    public TicketReservedResponse(Ticket ticket) {
        this.ticketId = ticket.getTicketId();
        this.qrCode = ticket.getQrCode();
        this.purchaseDate = ticket.getPurchaseDate();
        this.eventTitle = ticket.getEvent().getTitle();
        this.startDate = ticket.getEvent().getStartDate();
        this.endDate = ticket.getEvent().getEndDate();
    }
}
