package io.nugulticket.search.service;

import io.nugulticket.search.dto.searchEvents.SearchEventsResponse;
import io.nugulticket.search.dto.searchTickets.SearchTicketsResponse;
import io.nugulticket.search.entity.EventDocument;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SearchService {

    private final TicketService ticketService;
    private final ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 공연제목 키워드, 공연날짜, 공연장소, 카테고리로 공연을 검색하는 메서드 (엘라스틱 서치)
     * @param page 조회할 페이지 번호
     * @param size 한 페이지당 사이즈
     * @param title 티켓의 공연제목 중 키워드 검색
     * @param eventDate 티켓의 공연 시작일과 종료일 사이 날짜로 검색
     * @return Pageable한 SearchEventsResponse 반환
     */
    public Page<SearchEventsResponse> searchEvents(int page, int size, String title, LocalDate eventDate, String place, String category) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Criteria criteria = new Criteria();

        // 동적 쿼리 구성
        if (title != null) {
            criteria = criteria.or("title").fuzzy(title);
        }
        if (eventDate != null) {
            criteria = criteria.and("startDate").lessThanEqual(eventDate)
                    .and("endDate").greaterThanEqual(eventDate);
        }
        if (place != null) {
            criteria = criteria.or("place").fuzzy(place);
        }
        if (category != null) {
            criteria = criteria.or("category").fuzzy(category);
        }

        CriteriaQuery searchQuery = new CriteriaQuery(criteria).setPageable(pageable);

        // search 메서드를 사용하여 페이징된 결과를 얻음
        SearchHits<EventDocument> searchHits = elasticsearchTemplate.search(searchQuery, EventDocument.class);

        // SearchHits를 Page로 변환, SearchEventsResponse로 매핑
        return new PageImpl<>(
                searchHits.getSearchHits().stream()
                        .map(hit -> {
                            EventDocument eventDoc = hit.getContent();
                            return new SearchEventsResponse(
                                    eventDoc.getEventId(),
                                    eventDoc.getCategory(),
                                    eventDoc.getTitle(),
                                    eventDoc.getDescription(),
                                    eventDoc.getStartDate(),
                                    eventDoc.getEndDate(),
                                    eventDoc.getRuntime(),
                                    eventDoc.getViewRating(),
                                    eventDoc.getRating(),
                                    eventDoc.getPlace(),
                                    eventDoc.getBookAble(),
                                    eventDoc.getImageUrl()
                            );
                        })
                        .collect(Collectors.toList()),
                pageable,
                searchHits.getTotalHits()
        );
    }


    /**
     * 공연제목 키워드, 공연날짜로 검색하고 그 공연의 양도 가능한 티켓을 검색하는 메서드
     * @param page 조회할 페이지 번호
     * @param size 한 페이지당 사이즈
     * @param keyword   티켓의 공연제목 중 키워드 검색
     * @param eventDate 티켓의 공연 시작일과 종료일 사이 날짜로 검색
     * @return Pageable한 searchTransferableTicketsResponse 반환
     */
    @Transactional
    public Page<SearchTicketsResponse> searchTransferableTickets(int page, int size, String keyword, LocalDate eventDate) {
        // StopWatch 객체 생성
        StopWatch stopWatch = new StopWatch();
        // 측정 시작
        stopWatch.start();

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Ticket> tickets = ticketService.getTicketsFromKeywords(keyword, eventDate, pageable);

        // 측정 중단
        stopWatch.stop();
        // 소요 시간 출력
        System.out.println(stopWatch.prettyPrint());

        return tickets.map(SearchTicketsResponse::of);
    }
}

