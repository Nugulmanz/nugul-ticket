package io.nugulticket.dummy.seat;

import io.nugulticket.event.dto.createEvent.CreateEventRequest;
import io.nugulticket.eventtime.entity.EventTime;
import io.nugulticket.seat.entity.Seat;
import io.nugulticket.seat.enums.SeatType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DummySeatFactory {
    private void addSeat(List<Seat> seats, EventTime eventTime, int offset, int price, int seatSize, SeatType seatType) {
        for(int i = 0; i < seatSize; ++i) {
            seats.add(new Seat(eventTime, Integer.toString(offset + i + 1), price,seatType));
        }
    }

    public void createSeat(List<Seat> seats, EventTime eventTime, CreateEventRequest createEventRequest) {
        int offset = 0;

        int vipSeatSize = createEventRequest.getVipSeatCount();
        int rSeatSize = createEventRequest.getRSeatCount();
        int sSeatSize = createEventRequest.getSSeatCount();
        int aSeatSize = createEventRequest.getASeatCount();

        addSeat(seats, eventTime, offset, createEventRequest.getVipSeatPrice(), vipSeatSize, SeatType.VIP);
        offset += vipSeatSize;

        addSeat(seats, eventTime, offset, createEventRequest.getRSeatPrice(), rSeatSize, SeatType.R);
        offset += rSeatSize;

        addSeat(seats, eventTime, offset, createEventRequest.getSSeatPrice(), sSeatSize, SeatType.S);
        offset += sSeatSize;

        addSeat(seats, eventTime, offset, createEventRequest.getASeatPrice(), aSeatSize, SeatType.A);
    }

    public List<Seat> createDummySeats(List<EventTime> eventTimes, List<CreateEventRequest> requests) {
        List<Seat> seats = new ArrayList<>();

        for(int i = 0; i < requests.size(); ++i) {
            createSeat(seats, eventTimes.get(i), requests.get(i));
        }

        return seats;
    }
}
