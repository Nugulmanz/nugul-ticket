package io.nugulticket.search.service;

import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.search.dto.searchEvents.SearchEventsResponse;
import io.nugulticket.search.dto.searchTickets.SearchTicketsResponse;
import io.nugulticket.search.entity.EventDocument;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
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

    private final OpenSearchClient openSearchClient;

    /**
     * OpenSearch Java High-Level REST Client를 사용해 Elasticsearch에서 동적 쿼리를 생성하고, 특정 조건에 맞는 EventDocument를 검색하는 메서드
     * @param page      조회할 페이지 번호
     * @param size      한 페이지당 사이즈
     * @param title     티켓의 공연제목 중 키워드 검색
     * @param eventDate 티켓의 공연 시작일과 종료일 사이 날짜로 검색
     * @return Pageable한 SearchEventsResponse 반환
     */
    public Page<SearchEventsResponse> searchEvents(int page, int size, String title, LocalDate eventDate, String place, String category) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            List<SearchEventsResponse> results = new ArrayList<>();

            Query boolQuery = Query.of(q -> q
                    .bool(b -> {
                        if (title != null) {
                            b.should(m -> m.match(t -> t.field("title").query(FieldValue.of(title))));
                            b.should(m -> m.prefix(t -> t.field("titleInitials").value(title)));
                            b.minimumShouldMatch("1");
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

            SearchRequest request = SearchRequest.of(s -> s
                    .index("events_current")
                    .query(boolQuery)
                    .from((page - 1) * size)
                    .size(size)
            );

            SearchResponse<EventDocument> response = openSearchClient.search(request, EventDocument.class);

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

            long totalHits = response.hits().total().value();

            return new PageImpl<>(results, pageable, totalHits);

        } catch (IOException e) {
            throw new ApiException(ErrorStatus.SEARCH_QUERY_FAILURE);
        }
    }

    /**
     * 공연제목 키워드, 공연날짜로 검색하고 그 공연의 양도 가능한 티켓을 검색하는 메서드
     *
     * @param page      조회할 페이지 번호
     * @param size      한 페이지당 사이즈
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

