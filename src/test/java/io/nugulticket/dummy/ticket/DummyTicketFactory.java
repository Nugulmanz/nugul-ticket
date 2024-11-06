package io.nugulticket.dummy.ticket;

import com.github.javafaker.Faker;
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

    public List<Ticket> createDummyTickets(Faker faker, List<Seat> seats, List<User> users, int size) {
        List<Ticket> tickets = new ArrayList<>();
        int maxRange = Integer.min(size, seats.size());

        for (int i = 0; i < maxRange; ++i) {
            int next = faker.random().nextInt(users.size());

            User randUser = users.get(next);

            tickets.add(createTicket(seats.get(i),randUser));
        }

        return tickets;
    }
}
