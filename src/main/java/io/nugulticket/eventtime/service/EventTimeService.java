package io.nugulticket.eventtime.service;

import io.nugulticket.event.entity.Event;
import io.nugulticket.eventtime.entity.EventTime;
import io.nugulticket.eventtime.repository.EventTimeRepository;
import io.nugulticket.seat.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class EventTimeService {
    private final EventTimeRepository eventTimeRepository;
    private final SeatService seatService;

    /**
     * 이벤트 회차 정보를 생성하는 메서드
     * @param event 이벤트 객체
     * @param start 이벤트 시작 날짜
     * @param end 이벤트 종료 날짜
     * @param time 이벤트 시간
     * @param price 가격
     * @param vipSeatSize vip석 자리 수
     * @param rSeatSize r석 자리 수
     * @param aSeatSize a석 자리 수
     * @return 생성된 EventTime 리스트
     */
    @Transactional
    public List<EventTime> createEventTimes(Event event,
                                            LocalDate start,
                                            LocalDate end,
                                            LocalTime time,
                                            int price,
                                            int vipSeatSize,
                                            int rSeatSize,
                                            int aSeatSize) {
        LocalDate mid = start;
        List<EventTime> eventTimes = new ArrayList<>();
        int remainingSeat = vipSeatSize + rSeatSize + aSeatSize;

        while (mid.isBefore(end)) {
            EventTime eventTime = new EventTime(event, mid.atTime(time), remainingSeat);
            EventTime savedEventTime = eventTimeRepository.save(eventTime);

            eventTimes.add(savedEventTime);
            seatService.createSeat(savedEventTime, price, vipSeatSize, rSeatSize, aSeatSize, aSeatSize);

            mid = mid.plusDays(1);
        }

        return eventTimes;
    }
}
