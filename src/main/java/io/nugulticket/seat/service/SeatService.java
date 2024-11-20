package io.nugulticket.seat.service;

import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.eventtime.entity.EventTime;
import io.nugulticket.seat.dto.SeatResponse;
import io.nugulticket.seat.entity.Seat;
import io.nugulticket.seat.enums.SeatType;
import io.nugulticket.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class SeatService {
    private final SeatRepository seatRepository;

    /**
     * 좌석을 생성하는 메서드
     *
     * @param eventTime   공연 일자 + 시각
     * @param price       좌석당 가격
     * @param vipSeatSize vip 등급의 좌석 개수
     * @param rSeatSize   R 등급의 좌석 개수
     * @param sSeatSize   S 등급의 좌석 개수
     * @param aSeatSize   A 등급의 좌석 개수
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
     *
     * @param seats     데이터를 담을 List 객체
     * @param eventTime 공연이 진행되는 시간
     * @param offset    등급별 좌석 시작 Index offset
     * @param price     좌석 가격
     * @param seatSize  좌석 개수
     * @param seatType  좌석 타입 ( VIP, R, S, A )
     */
    private void addSeat(List<Seat> seats, EventTime eventTime, int offset, int price, int seatSize, SeatType seatType) {
        for (int i = 0; i < seatSize; ++i) {
            seats.add(new Seat(eventTime, Integer.toString(offset + i + 1), price, seatType));
        }
    }

    /**
     * 특정 공연의 회차 id를 받아 해당 공연 회차의 좌석 정보를 반환하는 메서드
     *
     * @param eventTimeId : 이벤트 회차 id
     * @return List<SeatResponse> : 전체 좌석 목록
     */
    public List<SeatResponse> getSeats(int eventTimeId) {
        List<Seat> seats = seatRepository.findByEventTimeId(eventTimeId);
        return seats.stream()
                .map(seat -> new SeatResponse(
                        seat.getId(),
                        seat.getSeatNumber(),
                        seat.isReserved(),
                        seat.getPrice(),
                        seat.getSeatType().toString()
                ))
                .collect(Collectors.toList());
    }


    // 해당 ID에 해당하는 Seat객체를 반환하는 메서드
    public Seat findSeatById(Long id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_SEAT));
    }
}
