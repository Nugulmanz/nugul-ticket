package io.nugulticket.seat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatResponse {
    private final Long seatId;
    private final String seatNumber;
    private final boolean isReserved;
    private final int price;
    private final String seatType;
}