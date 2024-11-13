package io.nugulticket.seat.service;

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

    /**
     * 좌석을 생성하는 메서드
     * @param eventTime 공연 일자 + 시각
     * @param price 좌석당 가격
     * @param vipSeatSize vip 등급의 좌석 개수
     * @param rSeatSize R 등급의 좌석 개수
     * @param sSeatSize S 등급의 좌석 개수
     * @param aSeatSize A 등급의 좌석 개수
     * @return 생성된 좌석 객체가 담긴 List
     */
    @Transactional
    public List<Seat> createSeat(EventTime eventTime, int price, int vipSeatSize, int rSeatSize, int sSeatSize, int aSeatSize) {
        List<Seat> seats = new ArrayList<>(vipSeatSize + rSeatSize + sSeatSize + aSeatSize);
        int offset = 0;

        addSeat(seats, eventTime, offset, price, vipSeatSize, SeatType.VIP);
        offset += vipSeatSize;

        addSeat(seats, eventTime, offset, price, rSeatSize, SeatType.R);
        offset += rSeatSize;

        addSeat(seats, eventTime, offset, price, sSeatSize, SeatType.S);
        offset += sSeatSize;

        addSeat(seats, eventTime, offset, price, aSeatSize, SeatType.A);

        return seatRepository.saveAll(seats);
    }

    /**
     * EventTime, price, seatSize, seatType을 사용하여 만든 Seat객체를 seats에 삽입하는 메서드
     * @param seats 데이터를 담을 List 객체
     * @param eventTime 공연이 진행되는 시간
     * @param offset 등급별 좌석 시작 Index offset
     * @param price 좌석 가격
     * @param seatSize 좌석 개수
     * @param seatType 좌석 타입 ( VIP, R, S, A )
     */
    private void addSeat(List<Seat> seats, EventTime eventTime, int offset, int price, int seatSize, SeatType seatType) {
        for(int i = 0; i < seatSize; ++i) {
            seats.add(new Seat(eventTime, Integer.toString(offset + i + 1), price,seatType));
        }
    }

    /**
     * 해당 ID에 해당하는 Seat객체를 반환하는 메서드
     * @param id 조회할 Seat Id
     * @return 해당 ID에 해당하는 Seat 객체
     */
    public Seat findSeatById(Long id) {
        return seatRepository.findById(id)
                // 예외 처리 나중에 수정(api response)
                .orElseThrow(() -> new IllegalArgumentException("해당 좌석을 찾을 수 없습니다."));
    }
}
