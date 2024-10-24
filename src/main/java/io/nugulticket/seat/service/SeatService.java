package io.nugulticket.seat.service;

import io.nugulticket.seat.entity.Seat;
import io.nugulticket.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SeatService {
    private final SeatRepository seatRepository;

    public Seat findSeatById(Long id) {
        return seatRepository.findById(id)
                // 예외 처리 나중에 수정(api response)
                .orElseThrow(() -> new IllegalArgumentException("해당 좌석을 찾을 수 없습니다."));

    }
}
