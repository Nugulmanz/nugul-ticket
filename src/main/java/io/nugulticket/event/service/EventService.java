package io.nugulticket.event.service;

import io.nugulticket.common.AuthUser;
import io.nugulticket.event.dto.CalenderEventResponse;
import io.nugulticket.event.dto.EventSimpleResponse;
import io.nugulticket.event.dto.createEvent.CreateEventRequest;
import io.nugulticket.event.dto.createEvent.CreateEventResponse;
import io.nugulticket.event.dto.getAllEvent.GetAllEventResponse;
import io.nugulticket.event.dto.getEvent.GetEventResponse;
import io.nugulticket.event.dto.updateEvent.UpdateEventRequest;
import io.nugulticket.event.dto.updateEvent.UpdateEventResponse;
import io.nugulticket.event.entity.Event;
import io.nugulticket.event.repository.EventRepository;
import io.nugulticket.eventtime.service.EventTimeService;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.enums.UserRole;
import io.nugulticket.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final UserService userService;
    private final EventRepository eventRepository;
    private final EventTimeService eventTimeService;

    public CreateEventResponse createEvent(AuthUser authUser, CreateEventRequest eventRequest) {
        int price = 140000;
        int vipSeatCount = 20;
        int rSeatCount = 20;
        int aSeatCount = 20;

        User user = userService.getUser(authUser.getId());

        Event event = new Event(user,eventRequest);

        Event savedEvent = eventRepository.save(event);

        eventTimeService.createEventTimes(event,
                eventRequest.getStartDate(),
                eventRequest.getEndDate(),
                LocalTime.now(),
                price,
                vipSeatCount,
                rSeatCount,
                aSeatCount);

        return new CreateEventResponse(savedEvent);
    }

    @Transactional
    public UpdateEventResponse updateEvent(AuthUser authUser, Long eventId, UpdateEventRequest eventRequest) {

        User user = userService.getUser(authUser.getId());

        if (!user.getUserRole().equals(UserRole.SELLER)) {
            throw new RuntimeException("수정 권한이 없습니다. SELLER 권한이 필요합니다.");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("해당 공연을 찾을 수 없습니다."));

        if (!event.getUser().equals(user)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }


        event.updateEvent(eventRequest);

        return new UpdateEventResponse(event);
    }

    @Transactional
    public void deleteEvent(AuthUser authUser, Long eventId) {

        User adminUser = userService.getUser(authUser.getId());

        if (!adminUser.getUserRole().equals(UserRole.ADMIN)) {
            throw new RuntimeException("삭제 권한이 없습니다. ADMIN 권한이 필요합니다.");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("해당 공연을 찾을 수 없습니다."));

        event.setIs_deleted(true);

        eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public GetEventResponse getEvent(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("해당 공연을 찾을 수 없습니다."));

        return new GetEventResponse(event);
    }

    @Transactional(readOnly = true)
    public List<GetAllEventResponse> getAllEvents() {

        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(GetAllEventResponse::new)
                .toList();
    }

    public Event getEventFromId(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(EntityNotFoundException::new);
    }

    public List<Event> getEventFromUserId(Long userId) {
        return eventRepository.findByUser_Id(userId);
    }

    public CalenderEventResponse calenderEvents(Integer year, Integer month) {
        LocalDate beginDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, beginDate.lengthOfMonth());

        List<Event> events = eventRepository.findByBetweenTwoDate(beginDate, endDate);

        List<EventSimpleResponse> simpleResponses = events.stream().map(EventSimpleResponse::of).toList();
        return CalenderEventResponse.of(simpleResponses);
    }

    public Page<Event> getEventsFromKeywords(String keyword, LocalDate eventDate, String place, String category, Pageable pageable) {
        return eventRepository.findByKeywords(keyword, eventDate, place, category, pageable);
    }
}
