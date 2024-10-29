package io.nugulticket.search.service;

import io.nugulticket.event.entity.Event;
import io.nugulticket.event.service.EventService;
import io.nugulticket.search.dto.searchEvents.SearchEventsResponse;
import io.nugulticket.search.dto.searchTickets.SearchTicketsResponse;
import io.nugulticket.search.entity.EventDocument;
import io.nugulticket.search.repository.EventSearchRepository;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class SearchService {

    private final EventService eventService;
    private final TicketService ticketService;

    private final EventSearchRepository eventSearchRepository;


//    @Transactional
//    public Page<SearchEventsResponse> searchEvents(int page, int size, String keyword, LocalDate eventDate, String place, String category) {
//        // StopWatch 객체 생성
//        StopWatch stopWatch = new StopWatch();
//        // 측정 시작
//        stopWatch.start();
//
//        Pageable pageable = PageRequest.of(page-1, size);
//        Page<Event> events = eventService.getEventsFromKeywords(keyword, eventDate, place, category, pageable);
//
//        // 측정 중단
//        stopWatch.stop();
//        // 소요 시간 출력
//        System.out.println(stopWatch.prettyPrint());
//
//        return events.map(SearchEventsResponse::of);
//    }

    /**
     * 공연제목 키워드, 공연날짜, 공연장소, 카테고리로 공연을 검색하는 메서드 (엘라스틱 서치)
     * @param page
     * @param size
     * @param keyword
     * @param eventDate
     * @param place
     * @param category
     * @return Pageable한 SearchEventsResponse 반환
     */
    public Page<SearchEventsResponse> searchEvents(int page, int size, String keyword, LocalDate eventDate, String place, String category) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Event> events = eventSearchRepository.findByKeywords(keyword, eventDate, place, category, pageable);
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
    public Page<SearchTicketsResponse> searchTransferableTickets(int page, int size, String keyword, LocalDate eventDate) {
        // StopWatch 객체 생성
        StopWatch stopWatch = new StopWatch();
        // 측정 시작
        stopWatch.start();

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Ticket> tickets = ticketService.getTicketsFromKeywords(keyword, eventDate, pageable);

        // 측정 중단
        stopWatch.stop();
        // 소요 시간 출력
        System.out.println(stopWatch.prettyPrint());

        return tickets.map(SearchTicketsResponse::of);
    }
}

