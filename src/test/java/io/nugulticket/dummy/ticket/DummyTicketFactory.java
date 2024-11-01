package io.nugulticket.dummy.ticket;

import io.nugulticket.seat.entity.Seat;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DummyTicketFactory {

    private Ticket createTicket(Seat seat, User user) {
        Ticket ticket = new Ticket();

        ticket.createTicket(seat.getEventTime().getEvent(), seat, user, "asdf");

        return ticket;
    }

    public List<Ticket> createDummyTickets(List<Seat> seats, List<User> users, int size) {
        List<Ticket> tickets = new ArrayList<>();
        int maxRange = Integer.min(Integer.min(size, seats.size()) , users.size());

        for (int i = 0; i < maxRange; ++i) {
            tickets.add(createTicket(seats.get(i), users.get(i)));
        }

        return tickets;
    }
}
