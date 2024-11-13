package io.nugulticket.ticket.dto.createTicket;

import io.nugulticket.event.entity.Event;
import io.nugulticket.seat.entity.Seat;
import io.nugulticket.seat.enums.SeatType;
import io.nugulticket.ticket.entity.Ticket;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateTicketResponse {
    private Long seatId;
    private Long eventId;
    private Long userId;
    private String seatNumber;
    private SeatType  seatType;
    private int price;
    private boolean isReserved;
    private LocalDateTime purchaseDate;

    // 이거 추가해주기
    private String title;

    public CreateTicketResponse(Seat seat, Event event, Ticket ticket, Long userId) {
        this.seatId=seat.getId();
        this.eventId=event.getEventId();
        this.userId=userId;
        this.seatNumber=seat.getSeatNumber();
        this.seatType=seat.getSeatType();
        this.price=seat.getPrice();
        this.isReserved=seat.isReserved();
        this.purchaseDate=ticket.getPurchaseDate();
    }

}
