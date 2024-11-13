package io.nugulticket.search.controller;

import io.nugulticket.common.apipayload.ApiResponse;
import io.nugulticket.search.service.SearchEventRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/event_rank/v1")
@RequiredArgsConstructor
public class SearchEventRankController {

    private final SearchEventRankService searchEventRankService;


    @PostMapping("/events/{eventId}")
    public ApiResponse<Map<String, Object>> addEvent(@RequestParam String eventTitle, @PathVariable Long eventId) {
        Map<String, Object> response = searchEventRankService.addKeywordAndGetScore(eventTitle,eventId);
        return ApiResponse.ok(response);
    }


    @GetMapping("/events/top")
    public ApiResponse<List<String>> getTopEvents(@RequestParam(defaultValue = "10") int count) {
        return ApiResponse.ok(searchEventRankService.getTopEvents(count));
    }
}
