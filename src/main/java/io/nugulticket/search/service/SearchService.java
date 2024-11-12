package io.nugulticket.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
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
    private final ElasticsearchClient elasticsearchClient;

    /**
     * Java API Client를 사용해 Elasticsearch에서 동적 쿼리를 생성하고, 특정 조건에 맞는 EventDocument를 검색하는 메서드
     * @param page 조회할 페이지 번호
     * @param size 한 페이지당 사이즈
     * @param title 티켓의 공연제목 중 키워드 검색
     * @param eventDate 티켓의 공연 시작일과 종료일 사이 날짜로 검색
     * @return Pageable한 SearchEventsResponse 반환
     */
    public Page<SearchEventsResponse> searchEvents(int page, int size, String title, LocalDate eventDate, String place, String category) {
        try {
            // 페이징을 위한 Pageable 객체 생성
            Pageable pageable = PageRequest.of(page - 1, size);
            List<SearchEventsResponse> results = new ArrayList<>();

            // 동적 쿼리 구성
            Query boolQuery = Query.of(q -> q
                    .bool(b -> {
                        if (title != null) {
                            // title 필드와 titleInitials 필드를 각각 검색
                            b.should(m -> m.match(t -> t.field("title").query(title))); // 일반 검색
                            b.should(m -> m.prefix(t -> t.field("titleInitials").value(title))); // 초성 검색
                            b.minimumShouldMatch("1"); // should 조건 중 하나 이상 일치해야 함
                        }
                        if (eventDate != null) {
                            b.filter(f -> f.range(r -> r.field("startDate").lte(JsonData.of(eventDate.toString()))));
                            b.filter(f -> f.range(r -> r.field("endDate").gte(JsonData.of(eventDate.toString()))));
                        }
                        if (place != null) {
                            b.should(m -> m.match(t -> t.field("place").query(FieldValue.of(place))));
                        }
                        if (category != null) {
                            b.should(m -> m.match(t -> t.field("category").query(FieldValue.of(category))));
                        }
                        return b;
                    })
            );

            // 검색 요청 생성 - 알리아스 설정된 인덱스에 요청
            SearchRequest request = SearchRequest.of(s -> s
                    .index("events_current") // 알리아스 사용
                    .query(boolQuery)
                    .from((page - 1) * size) // 페이징
                    .size(size)
            );

            // Elasticsearch에 요청 보내기
            // elasticsearchClient.search() 메서드를 통해 request를 보내고, 결과를 EventDocument 타입의 SearchResponse로 받아옴
            SearchResponse<EventDocument> response = elasticsearchClient.search(request, EventDocument.class);

            // SearchHits를 통해 검색 결과를 파싱
            // hits()는 Elasticsearch에서 검색된 결과 목록을 가져오는 메서드, 실제 검색된 결과 목록을 반환
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

        } catch (IOException e) {
            throw new ApiException(ErrorStatus.SEARCH_QUERY_FAILURE);
        }
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

