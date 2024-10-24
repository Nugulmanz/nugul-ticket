package io.nugulticket.ticket.dto.response;

import io.nugulticket.ticket.entity.Ticket;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class TicketCompletedResponse {
    private Long ticketId;
    private String eventTitle;
    private LocalDateTime purchaseDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String qrCode;

    public TicketCompletedResponse(Ticket ticket) {
        this.ticketId = ticket.getTicketId();
        this.qrCode = ticket.getQrCode();
        this.purchaseDate = ticket.getPurchaseDate();
        this.eventTitle = ticket.getEvent().getTitle();
        this.startDate = ticket.getEvent().getStartDate();  // 공연 시작일 할당
        this.endDate = ticket.getEvent().getEndDate();

    }
}
