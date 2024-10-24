package io.nugulticket.ticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TicketTransferCancelResponse {
    Long ticketId;

    public static TicketTransferCancelResponse of(Long ticketId) {
        return new TicketTransferCancelResponse(ticketId);
    }
}
