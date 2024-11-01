package io.nugulticket;

import io.nugulticket.common.utils.JwtUtil;
import io.nugulticket.dummy.dto.DummyEventRequestFactory;
import io.nugulticket.dummy.event.DummyEventFactory;
import io.nugulticket.event.dto.createEvent.CreateEventRequest;
import io.nugulticket.event.entity.Event;
import io.nugulticket.eventtime.entity.EventTime;
import io.nugulticket.seat.entity.Seat;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class CreateDummyDataTest {
    @Autowired
    DummyDataFactory dummyDataFactory;

    @Autowired
    DummyEventRequestFactory dummyEventRequestFactory;

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    private DummyEventFactory dummyEventFactory;

    @Test
    void contextLoads() {
    }

    @Test
    void createDummyUsers() {
    }

    @Test
    void createDummyDatas() {
        int userSize = 100;
        int eventSize = 100;
        int ticketSize = 1000;

        List<CreateEventRequest> createEventRequests = dummyEventRequestFactory.createEventsRequests(dummyDataFactory.getFaker(), eventSize);

        List<User> users = dummyDataFactory.createDummyUser(userSize);
        List<Event> events = dummyDataFactory.createDummyEvent(users, createEventRequests);
        List<EventTime> eventTimes = dummyDataFactory.createDummyEventTime(events, createEventRequests);
        List<Seat> seats = dummyDataFactory.createDummySeats(eventTimes, createEventRequests);
        List<Ticket> tickets = dummyDataFactory.createDummyTickets(seats, users, ticketSize);
    }

    @Test
    void getToken() {
        System.out.println(dummyDataFactory.getTokens());
    }
}
