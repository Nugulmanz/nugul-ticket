package io.nugulticket.ticket.dto.response;

import io.nugulticket.ticket.entity.TicketTransfer;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TicketTransferApplyResponse {
    private Long ticketId;
    private Long sellerId;
    private Long ticketTransferId;
    private Integer price;
    private LocalDateTime transferAt;

    public TicketTransferApplyResponse(final TicketTransfer ticketTransfer) {
        this.ticketId = ticketTransfer.getTicket().getTicketId();
        this.sellerId = ticketTransfer.getTicket().getBuyerId();
        this.ticketTransferId = ticketTransfer.getId();
        this.price = ticketTransfer.getPrice();
        this.transferAt = ticketTransfer.getTransferAt();
    }
}
