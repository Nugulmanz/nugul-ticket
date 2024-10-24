package io.nugulticket.event.service;

import io.nugulticket.event.dto.createEvent.CreateEventRequest;
import io.nugulticket.event.dto.createEvent.CreateEventResponse;
import io.nugulticket.event.dto.getAllEvent.GetAllEventResponse;
import io.nugulticket.event.dto.getEvent.GetEventResponse;
import io.nugulticket.event.dto.updateEvent.UpdateEventRequest;
import io.nugulticket.event.dto.updateEvent.UpdateEventResponse;
import io.nugulticket.event.entity.Event;
import io.nugulticket.event.repository.EventRepository;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.enums.UserRole;
import io.nugulticket.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final UserService userService;
    private final EventRepository eventRepository;

    @Transactional
    public CreateEventResponse createEvent(Long userId, CreateEventRequest eventRequest) {

        User user = userService.getUser(userId);

        Event event = new Event(user,eventRequest);

        Event savedEvent = eventRepository.save(event);

        return new CreateEventResponse(savedEvent);
    }

    @Transactional
    public UpdateEventResponse updateEvent(Long userId, Long eventId, UpdateEventRequest eventRequest) {

        User user = userService.getUser(userId);

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
    public void deleteEvent(Long adminId, Long eventId) {

        User adminUser = userService.getUser(adminId);

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
}
