package io.nugulticket.dummy.event;

import com.github.javafaker.Faker;
import io.nugulticket.event.dto.createEvent.CreateEventRequest;
import io.nugulticket.event.entity.Event;
import io.nugulticket.user.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DummyEventFactory {

    public List<Event> createDummyEvent(List<User> users, Faker faker, List<CreateEventRequest> requests) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        List<Event> events = new ArrayList<Event>();

        for (CreateEventRequest request : requests) {
            int next = faker.random().nextInt(users.size());

            User randUser = users.get(next);
            Event event = new Event(randUser, request, "url");

            events.add(event);
        }

        return events;
    }
}
