package io.nugulticket.search.controller;

import io.nugulticket.common.apipayload.ApiResponse;
import io.nugulticket.search.dto.searchEvents.SearchEventsResponse;
import io.nugulticket.search.dto.searchTickets.SearchTicketsResponse;
import io.nugulticket.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    //키워드로 공연 검색
    @GetMapping("/v1/events")
    public ApiResponse<Page<SearchEventsResponse>> searchEvents(@RequestParam(defaultValue = "1", required = false) int page,
                                                         @RequestParam(defaultValue = "10", required = false) int size,
                                                         @RequestParam(required = false) String title,
                                                         @RequestParam(required = false) LocalDate eventDate,
                                                         @RequestParam(required = false) String place,
                                                         @RequestParam(required = false) String category) throws IOException {
        return ApiResponse.ok(searchService.searchEvents(page, size, title, eventDate, place, category));
    }

    //검색하는 공연의 양도 티켓 검색
    @GetMapping("/v1/tickets")
    public ApiResponse<Page<SearchTicketsResponse>> searchTransferableTickets (@RequestParam(defaultValue = "1", required = false) int page,
                                                                              @RequestParam(defaultValue = "10", required = false) int size,
                                                                              @RequestParam(required = false) String keyword,
                                                                              @RequestParam(required = false) LocalDate eventDate) {
        return ApiResponse.ok(searchService.searchTransferableTickets(page, size, keyword, eventDate));
    }




}
