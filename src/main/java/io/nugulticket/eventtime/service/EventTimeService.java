package io.nugulticket.eventtime.service;

import io.nugulticket.event.entity.Event;
import io.nugulticket.eventtime.entity.EventTime;
import io.nugulticket.eventtime.repository.EventTimeRepository;
import io.nugulticket.seat.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
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
            seatService.creatSeat(savedEventTime, price, vipSeatSize, rSeatSize, aSeatSize);

            mid = mid.plusDays(1);
        }

        return eventTimes;
    }
}
