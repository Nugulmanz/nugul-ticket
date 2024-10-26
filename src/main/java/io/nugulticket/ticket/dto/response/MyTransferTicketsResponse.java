package io.nugulticket.ticket.dto.response;

import io.nugulticket.ticket.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MyTransferTicketsResponse {

    List<TicketDetailResponse> tickets;

    // Ticket 정보를 정제 하기 전 / Join fetch 꼭 진행 해야함!
    public static MyTransferTicketsResponse of(final List<Ticket> ticketList) {
        return new MyTransferTicketsResponse(ticketList.stream().map(TicketDetailResponse::of).toList());
    }
}
