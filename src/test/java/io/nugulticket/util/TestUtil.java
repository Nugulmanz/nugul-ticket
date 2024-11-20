package io.nugulticket.util;

import io.nugulticket.common.AuthUser;
import io.nugulticket.event.dto.createEvent.CreateEventRequest;
import io.nugulticket.event.entity.Event;
import io.nugulticket.eventtime.entity.EventTime;
import io.nugulticket.seat.entity.Seat;
import io.nugulticket.seat.enums.SeatType;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.entity.TicketTransfer;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.enums.LoginType;
import io.nugulticket.user.enums.UserRole;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestUtil {
    public static User getUser() {
        return new User("a@b.com", "password", "Name", "Nickname", "010-0000-0000", UserRole.USER, LoginType.LOCAL, true);
    }

    public static User getUser(long id) {
        User user = getUser();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    public static User getUserIdAndSocial(long id) {
        User user = getUser();
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "loginType", LoginType.SOCIAL);
        return user;
    }

    public static User getUserIdAndUserRole(long id, UserRole userRole) {
        User user = getUser(id);
        ReflectionTestUtils.setField(user, "userRole", userRole);
        return user;
    }

    public static CreateEventRequest getRequest() {
        String categori = "뮤지컬";
        int runtime = 180;
        int viewRating = 1;
        double rating = 4.5;
        int vipSeatCount = 1;
        int rSeatCount = 1;
        int sSeatCount = 1;
        int aSeatCount = 1;
        int vipSeatPrice = 1;
        int rSeatPrice = 1;
        int sSeatPrice = 1;
        int aSeatPrice = 1;

        return CreateEventRequest.builder()
                .title("Title")
                .place("Place")
                .category(categori)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .runtime(Integer.toString(runtime))
                .viewRating(Integer.toString(viewRating))
                .rating(rating)
                .bookAble(true)
                .vipSeatCount(vipSeatCount)
                .rSeatCount(rSeatCount)
                .sSeatCount(sSeatCount)
                .aSeatCount(aSeatCount)
                .vipSeatPrice(vipSeatPrice)
                .rSeatPrice(rSeatPrice)
                .sSeatPrice(sSeatPrice)
                .aSeatPrice(aSeatPrice)
                .build();
    }

    public static EventTime getEventTime(Long id) {
        EventTime eventTime = new EventTime(getEvent(id), LocalDateTime.now(), 1);

        ReflectionTestUtils.setField(eventTime, "id", 1);
        return eventTime;
    }

    public static Seat getSeat(Long id) {
        Seat seat = new Seat(getEventTime(id), "1", 1, SeatType.VIP);

        ReflectionTestUtils.setField(seat, "id", id);
        return seat;
    }

    public static Event getEvent(Long id) {
        Event event = new Event(getUser(1L), getRequest(), " ");

        ReflectionTestUtils.setField(event, "eventId", id);
        return event;
    }

    public static Ticket getTicket(Long id) {
        Ticket ticket = new Ticket();

        ticket.createTicket(getEvent(id), getSeat(id), getUser(id), " ");
        ReflectionTestUtils.setField(ticket, "ticketId", id);
        return ticket;
    }

    public static TicketTransfer getTicketTransfer(Long id, Long userId, Ticket ticket) {
        TicketTransfer transfer = new TicketTransfer(ticket, userId);

        ReflectionTestUtils.setField(transfer, "id", id);
        return transfer;
    }

    public static AuthUser getAuthUser(User user) {

        return new AuthUser(user.getId(), user.getAddress(), user.getUserRole());
    }
}
