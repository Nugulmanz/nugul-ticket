package io.nugulticket.seat.entity;

import io.nugulticket.eventtime.entity.EventTime;
import io.nugulticket.seat.enums.SeatType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "seat")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_time_id", nullable = false)
    private EventTime eventTime;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(name = "is_reserved", nullable = false)
    boolean isReserved;

    @Column(nullable = false)
    private int price;

    @Column(name = "category_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    private boolean isDelete = false;

    public void seatReserved() {
        this.isReserved = true;
    }

    public void rollbackReserve() {
        this.isReserved = false;
    }

    public Seat(EventTime eventTime, String seatNumber, int price, SeatType seatType) {
        this.eventTime = eventTime;
        this.seatNumber = seatNumber;
        this.isReserved = false;
        this.price = price;
        this.seatType = seatType;
    }
}
