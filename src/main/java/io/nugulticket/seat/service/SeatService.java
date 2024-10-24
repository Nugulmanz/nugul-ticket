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
    public List<Seat> creatSeat(EventTime eventTime, int price, int vipSeatSize, int rSeatSize, int aSeatSize) {
        List<Seat> seats = new ArrayList<>(vipSeatSize + rSeatSize + aSeatSize);

        for (int i = 0; i < vipSeatSize; i++) {
            seats.add(new Seat(eventTime, Integer.toString(i + 1), price, SeatType.VIP));
        }

        for (int i = 0; i < rSeatSize; i++) {
            seats.add(new Seat(eventTime, Integer.toString(vipSeatSize + i + 1), price, SeatType.R));
        }

        for (int i = 0; i < aSeatSize; i++) {
            seats.add(new Seat(eventTime, Integer.toString(vipSeatSize + rSeatSize + i + 1), price, SeatType.A));
        }

        return seatRepository.saveAll(seats);
    }

    public Seat findSeatById(Long id) {
        return seatRepository.findById(id)
                // 예외 처리 나중에 수정(api response)
                .orElseThrow(() -> new IllegalArgumentException("해당 좌석을 찾을 수 없습니다."));
    }
}
