package io.nugulticket.ticket.dto.response;

import io.nugulticket.event.entity.Event;
import io.nugulticket.seat.entity.Seat;
import io.nugulticket.seat.enums.SeatType;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TicketDetailResponse {
    Long ticketId;
    String eventTitle;
    String eventPlace;
    LocalDate date;
    LocalTime time;
    Long seatId;
    SeatType seatType;
    int price;
    TicketStatus ticketStatus;

    public static TicketDetailResponse of(Ticket ticket) {
        Event event = ticket.getEvent();
        Seat seat = ticket.getSeat();

        return new TicketDetailResponse(
                ticket.getTicketId(),
                event.getTitle(),
                event.getPlace(),
                seat.getEventTime().getDateTime().toLocalDate(),
                seat.getEventTime().getDateTime().toLocalTime(),
                seat.getId(),
                seat.getSeatType(),
                seat.getPrice(),
                ticket.getStatus()
        );
    }
}
