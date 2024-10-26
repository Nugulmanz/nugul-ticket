package io.nugulticket.search.dto.searchTransferableTickets;

import io.nugulticket.event.entity.Event;
import io.nugulticket.seat.entity.Seat;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class searchTransferableTicketsResponse {

    private String title;
    private String category;
    private String seatNumber;
    private int price; //양도가격 - 티켓 본 가격
    private Long userId; //양도하는 사람 아이디

    public static searchTransferableTicketsResponse of(Ticket ticket) {
        Event event = ticket.getEvent();
        Seat seat = ticket.getSeat();
        User user = ticket.getUser();
        return new searchTransferableTicketsResponse(
                event.getTitle(),
                event.getCategory(),
                seat.getSeatNumber(),
                seat.getPrice(),
                user.getId()
        );
    }

}
