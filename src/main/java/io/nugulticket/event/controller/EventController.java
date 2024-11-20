package io.nugulticket.event.controller;

import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.ApiResponse;
import io.nugulticket.event.dto.CalenderEventResponse;
import io.nugulticket.event.dto.createEvent.CreateEventRequest;
import io.nugulticket.event.dto.createEvent.CreateEventResponse;
import io.nugulticket.event.dto.getAllEvent.GetAllEventResponse;
import io.nugulticket.event.dto.getEvent.GetEventResponse;
import io.nugulticket.event.dto.updateEvent.UpdateEventRequest;
import io.nugulticket.event.dto.updateEvent.UpdateEventResponse;
import io.nugulticket.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/events/v1")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ApiResponse<CreateEventResponse> createEvent(
            @AuthenticationPrincipal AuthUser authUser,
            @ModelAttribute CreateEventRequest eventRequest) {
        CreateEventResponse response = eventService.createEvent(authUser, eventRequest, eventRequest.getImage());
        return ApiResponse.ok(response);
    }

    @PatchMapping("/{eventId}")
    public ApiResponse<UpdateEventResponse> updateEvent(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long eventId,
            @ModelAttribute UpdateEventRequest eventRequest) {

        UpdateEventResponse response = eventService.updateEvent(authUser, eventId, eventRequest);
        return ApiResponse.ok(response);
    }

    @DeleteMapping("/{eventId}")
    public ApiResponse<Void> deleteEvent(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long eventId) {

        eventService.deleteEvent(authUser, eventId);
        return ApiResponse.ok(null);
    }

    // 공연 단건 조회
    @GetMapping("/{eventId}")
    public ApiResponse<GetEventResponse> getEvent(@PathVariable Long eventId) {
        GetEventResponse response = eventService.getEvent(eventId);
        return ApiResponse.ok(response);
    }

    // 공연 전체 조회
    @GetMapping
    public ApiResponse<List<GetAllEventResponse>> getAllEvents() {
        List<GetAllEventResponse> response = eventService.getAllEvents();
        return ApiResponse.ok(response);
    }

    @GetMapping("/calender")
    public ApiResponse<CalenderEventResponse> calenderEvents(
            @RequestParam(required = true) Integer year,
            @RequestParam(required = true) Integer month) {

        return ApiResponse.ok(eventService.calenderEvents(year, month));
    }
}
