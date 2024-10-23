package io.nugulticket.event.controller;

import io.nugulticket.event.dto.createEvent.CreateEventRequest;
import io.nugulticket.event.dto.createEvent.CreateEventResponse;
import io.nugulticket.event.dto.getAllEvent.GetAllEventResponse;
import io.nugulticket.event.dto.getEvent.GetEventResponse;
import io.nugulticket.event.dto.updateEvent.UpdateEventRequest;
import io.nugulticket.event.dto.updateEvent.UpdateEventResponse;
import io.nugulticket.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/v1")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<CreateEventResponse> createEvent(
            @RequestParam Long userId,
            @RequestBody CreateEventRequest eventRequest) {

        CreateEventResponse response = eventService.createEvent(userId, eventRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<UpdateEventResponse> updateEvent(
            @RequestParam Long userId,
            @PathVariable Long eventId,
            @RequestBody UpdateEventRequest eventRequest) {

        UpdateEventResponse response = eventService.updateEvent(userId, eventId, eventRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @RequestParam Long adminId,
            @PathVariable Long eventId) {

        eventService.deleteEvent(adminId, eventId);
        return ResponseEntity.ok().build();
    }

    // 공연 단건 조회
    @GetMapping("/{eventId}")
    public ResponseEntity<GetEventResponse> getEvent(@PathVariable Long eventId) {
        GetEventResponse response = eventService.getEvent(eventId);
        return ResponseEntity.ok(response);
    }

    // 공연 전체 조회
    @GetMapping
    public ResponseEntity<List<GetAllEventResponse>> getAllEvents() {
        List<GetAllEventResponse> response = eventService.getAllEvents();
        return ResponseEntity.ok(response);
    }
}
