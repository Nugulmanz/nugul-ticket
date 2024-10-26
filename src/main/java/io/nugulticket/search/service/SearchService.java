package io.nugulticket.search.service;

import io.nugulticket.event.entity.Event;
import io.nugulticket.event.service.EventService;
import io.nugulticket.search.dto.searchEvents.SearchEventsResponse;
import io.nugulticket.search.dto.searchTransferableTickets.searchTransferableTicketsResponse;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class SearchService {

    private final EventService eventService;
    private final TicketService ticketService;

    /**
     * 공연제목 키워드, 공연날짜, 공연장소, 카테고리로 공연을 검색하는 메서드
     * @param page
     * @param size
     * @param keyword 공연제목 중 키워드 검색
     * @param eventDate 공연 시작일과 종료일 사이 날짜로 검색
     * @param place 공연 장소
     * @param category 공연 카테고리
     * @return Pageable한 SearchEventsResponse 반환
     */
    @Transactional
    public Page<SearchEventsResponse> searchEvents(int page, int size, String keyword, LocalDate eventDate, String place, String category) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Event> events = eventService.getEventsFromKeywords(keyword, eventDate, place, category, pageable);
        return events.map(SearchEventsResponse::of);
    }

    /**
     * 공연제목 키워드, 공연날짜로 검색하고 그 공연의 양도 가능한 티켓을 검색하는 메서드
     * @param page
     * @param size
     * @param keyword 티켓의 공연제목 중 키워드 검색
     * @param eventDate 티켓의 공연 시작일과 종료일 사이 날짜로 검색
     * @return Pageable한 searchTransferableTicketsResponse 반환
     */
    @Transactional
    public Page<searchTransferableTicketsResponse> searchTransferableTickets(int page, int size, String keyword, LocalDate eventDate) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Ticket> tickets = ticketService.getTicketsFromKeywords(keyword, eventDate, pageable);
        return tickets.map(searchTransferableTicketsResponse::of);
    }
}
