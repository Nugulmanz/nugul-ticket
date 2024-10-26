package io.nugulticket.seat.service;

import io.nugulticket.event.entity.Event;
import io.nugulticket.eventtime.entity.EventTime;
import io.nugulticket.seat.entity.Seat;
import io.nugulticket.seat.enums.SeatType;
import io.nugulticket.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class SeatService {
    private final SeatRepository seatRepository;

    @Transactional
    public List<Seat> creatSeat(EventTime eventTime, int price, int vipSeatSize, int rSeatSize, int sSeatSize, int aSeatSize) {
        List<Seat> seats = new ArrayList<>(vipSeatSize + rSeatSize + sSeatSize + aSeatSize);
        int offset = 0;

        addSeat(seats, eventTime, offset, price, vipSeatSize, SeatType.VIP);
        offset += vipSeatSize;

        addSeat(seats, eventTime, offset, price, vipSeatSize, SeatType.R);
        offset += rSeatSize;

        addSeat(seats, eventTime, offset, price, vipSeatSize, SeatType.S);
        offset += sSeatSize;

        addSeat(seats, eventTime, offset, price, vipSeatSize, SeatType.A);

        return seatRepository.saveAll(seats);
    }

    private void addSeat(List<Seat> seats, EventTime eventTime, int offset, int price, int seatSize, SeatType seatType) {
        for(int i = 0; i < seatSize; ++i) {
            seats.add(new Seat(eventTime, Integer.toString(offset + i + 1), price,seatType));
        }
    }

    public Seat findSeatById(Long id) {
        return seatRepository.findById(id)
                // 예외 처리 나중에 수정(api response)
                .orElseThrow(() -> new IllegalArgumentException("해당 좌석을 찾을 수 없습니다."));
    }
}
