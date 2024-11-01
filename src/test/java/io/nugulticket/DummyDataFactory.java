package io.nugulticket;

import com.github.javafaker.Faker;
import io.nugulticket.common.utils.JwtUtil;
import io.nugulticket.dummy.event.DummyEventFactory;
import io.nugulticket.dummy.eventtime.DummyEventTimeFactory;
import io.nugulticket.dummy.seat.DummySeatFactory;
import io.nugulticket.dummy.ticket.DummyTicketFactory;
import io.nugulticket.dummy.user.DummyUserFactory;
import io.nugulticket.event.dto.createEvent.CreateEventRequest;
import io.nugulticket.event.entity.Event;
import io.nugulticket.event.repository.EventRepository;
import io.nugulticket.eventtime.entity.EventTime;
import io.nugulticket.eventtime.repository.EventTimeRepository;
import io.nugulticket.seat.entity.Seat;
import io.nugulticket.seat.repository.SeatRepository;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.repository.TicketRepository;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DummyDataFactory {

    @Autowired
    JwtUtil util;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DummyUserFactory dummyUserFactory;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    DummyEventFactory dummyEventFactory;

    @Autowired
    DummyEventTimeFactory dummyEventTimeFactory;

    @Autowired
    EventTimeRepository eventTimeRepository;

    @Autowired
    DummySeatFactory dummySeatFactory;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    DummyTicketFactory dummyTicketFactory;

    @Autowired
    TicketRepository ticketRepository;

    private final Faker faker = new Faker();

    @Transactional
    public List<User> createDummyUser(int size) {
        List<User> users = dummyUserFactory.createDummyUser(faker, size);

        return userRepository.saveAll(users);
    }

    @Transactional
    public List<Event> createDummyEvent(List<User> users, List<CreateEventRequest> createEventRequests) {
        List<Event> events = dummyEventFactory.createDummyEvent(users, faker, createEventRequests);

        return eventRepository.saveAll(events);
    }

    @Transactional
    public List<EventTime> createDummyEventTime(List<Event> events, List<CreateEventRequest> createEventRequests) {
        List<EventTime> eventTimes = dummyEventTimeFactory.createDummyEventTime(events, createEventRequests);

        return eventTimeRepository.saveAll(eventTimes);
    }

    @Transactional
    public List<Seat> createDummySeats(List<EventTime> eventTimes, List<CreateEventRequest> createEventRequests) {
        List<Seat> seats = dummySeatFactory.createDummySeats(eventTimes, createEventRequests);

        return seatRepository.saveAll(seats);
    }

    @Transactional
    public List<Ticket> createDummyTickets(List<Seat> seats, List<User> users, int size) {
        List<Ticket> tickets = dummyTicketFactory.createDummyTickets(seats, users, size);

        return ticketRepository.saveAll(tickets);
    }

    @Transactional(readOnly = true)
    public String getTokens() {
        List<User> users = userRepository.findAll();
        StringBuilder result = new StringBuilder();

        for (User user : users) {
            String str = util.createToken(user.getId(), user.getEmail(), user.getUserRole());

            result.append(str).append("\n");
        }

        return result.toString();
    }

    public Faker getFaker() {
        return faker;
    }
}
