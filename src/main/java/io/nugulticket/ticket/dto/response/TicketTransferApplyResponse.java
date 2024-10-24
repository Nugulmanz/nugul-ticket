package io.nugulticket.ticket.dto.response;

import io.nugulticket.ticket.entity.TicketTransfer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TicketTransferApplyResponse {
    private Long ticketId;
    private Long sellerId;
    private Long ticketTransferId;
    private Integer price;
    private LocalDateTime transferAt;

    public static TicketTransferApplyResponse of(TicketTransfer ticketTransfer) {
        return new TicketTransferApplyResponse(
                ticketTransfer.getTicket().getTicketId(),
                ticketTransfer.getTicket().getUser().getId(),
                ticketTransfer.getId(),
                ticketTransfer.getPrice(),
                ticketTransfer.getTransferAt()
        );
    }
}
