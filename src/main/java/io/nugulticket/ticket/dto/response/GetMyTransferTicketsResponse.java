package io.nugulticket.ticket.dto.response;

import io.nugulticket.ticket.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class GetMyTransferTicketsResponse {

    List<Ticket> ticketList;

    // Ticket 정보를 정제 하기 전 / Join fetch 꼭 진행 해야함!
    public GetMyTransferTicketsResponse(final List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }
}
