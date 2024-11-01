package io.nugulticket.dummy.eventtime;

import io.nugulticket.event.dto.createEvent.CreateEventRequest;
import io.nugulticket.event.entity.Event;
import io.nugulticket.eventtime.entity.EventTime;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DummyEventTimeFactory {

    private EventTime createEventTime(Event event, CreateEventRequest createEventRequest) {
        int remainingSeat = createEventRequest.getASeatCount() + createEventRequest.getRSeatCount()
                + createEventRequest.getSSeatCount() + createEventRequest.getVipSeatCount();
        LocalTime time = LocalTime.now();

        return new EventTime(event, createEventRequest.getStartDate().atTime(time), remainingSeat);
    }

    public List<EventTime> createDummyEventTime(List<Event> events, List<CreateEventRequest> createEventRequests) {
        List<EventTime> eventTimes = new ArrayList<>();

        for (int i = 0; i < events.size(); i++) {
            eventTimes.add(createEventTime(events.get(i), createEventRequests.get(i)));
        }

        return eventTimes;
    }
}
