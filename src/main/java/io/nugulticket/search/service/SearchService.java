package io.nugulticket.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SearchService {

    private final TicketService ticketService;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ElasticsearchClient elasticsearchClient;

    /**
     * 공연제목 키워드, 공연날짜, 공연장소, 카테고리로 공연을 검색하는 메서드 (엘라스틱 서치)
     * @param page 조회할 페이지 번호
     * @param size 한 페이지당 사이즈
     * @param title 티켓의 공연제목 중 키워드 검색
     * @param eventDate 티켓의 공연 시작일과 종료일 사이 날짜로 검색
     * @return Pageable한 SearchEventsResponse 반환
     */
    public Page<SearchEventsResponse> searchEvents(int page, int size, String title, LocalDate eventDate, String place, String category) throws IOException {
        // 페이징을 위한 Pageable 객체 생성
        Pageable pageable = PageRequest.of(page - 1, size);
        List<SearchEventsResponse> results = new ArrayList<>();

        // 동적 쿼리 구성
        Query boolQuery = Query.of(q -> q
                .bool(b -> {
                    if (title != null) {
                        b.must(m -> m.match(t -> t.field("title").query(FieldValue.of(title))));
                    }
                    if (eventDate != null) {
                        b.filter(f -> f.range(r -> r.field("startDate").lte(JsonData.of(eventDate.toString()))));
                        b.filter(f -> f.range(r -> r.field("endDate").gte(JsonData.of(eventDate.toString()))));
                    }
                    if (place != null) {
                        b.must(m -> m.match(t -> t.field("place").query(FieldValue.of(place))));
                    }
                    if (category != null) {
                        b.must(m -> m.match(t -> t.field("category").query(FieldValue.of(category))));
                    }
                    return b;
                })
        );

        // 검색 요청 생성
        SearchRequest request = SearchRequest.of(s -> s
                .index("events")
                .query(boolQuery)
                .from((page - 1) * size) // 시작 위치
                .size(size)               // 페이지당 결과 개수
        );

        // Elasticsearch에 요청 보내기
        SearchResponse<EventDocument> response = elasticsearchClient.search(request, EventDocument.class);

        // SearchHits를 통해 검색 결과를 파싱
        for (Hit<EventDocument> hit : response.hits().hits()) {
            EventDocument eventDoc = hit.source();
            results.add(new SearchEventsResponse(
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
            ));
        }

        // 검색 결과 전체 개수
        long totalHits = response.hits().total().value();

        // PageImpl을 통해 Page 형식으로 반환
        return new PageImpl<>(results, pageable, totalHits);
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

