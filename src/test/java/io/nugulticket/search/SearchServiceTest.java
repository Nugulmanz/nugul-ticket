package io.nugulticket.search;

import io.nugulticket.search.dto.searchEvents.SearchEventsResponse;
import io.nugulticket.search.dto.searchTickets.SearchTicketsResponse;
import io.nugulticket.search.entity.EventDocument;
import io.nugulticket.search.service.SearchService;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.service.TicketService;
import io.nugulticket.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {
    @InjectMocks
    private SearchService searchService;
    @Mock
    private TicketService ticketService;
    @Mock
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void testSearchEvents_withCriteria_shouldReturnPagedResults() throws IOException {
        // Given
        int page = 1;
        int size = 5;
        String title = "Sample Event";
        LocalDate eventDate = LocalDate.of(2024, 11, 5);
        String place = "Sample Place";
        String category = "Music";

        Pageable pageable = PageRequest.of(page - 1, size);

        // 검색 결과로 반환할 Mock 데이터 생성
        EventDocument eventDoc = new EventDocument(
                1L, category, title, "Sample Description",
                "2024-11-04", "2024-12-04",
                "90 minutes", "All Ages", 4.5, place, true, "image.jpg"
        );

        SearchHit<EventDocument> searchHit = mock(SearchHit.class);
        when(searchHit.getContent()).thenReturn(eventDoc);

        // SearchHits Mock 생성
        SearchHits<EventDocument> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(List.of(searchHit));
        when(searchHits.getTotalHits()).thenReturn(1L);


        // elasticsearchTemplate의 search 메서드에 대한 Mock 설정
        when(elasticsearchTemplate.search(any(CriteriaQuery.class), eq(EventDocument.class)))
                .thenReturn(searchHits);

        // When
        Page<SearchEventsResponse> resultPage = searchService.searchEvents(page, size, title, eventDate, place, category);

        // Then
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getTotalElements()).isEqualTo(1);

        SearchEventsResponse response = resultPage.getContent().get(0);
        assertThat(response.getTitle()).isEqualTo(title);
        assertThat(response.getCategory()).isEqualTo(category);
        assertThat(response.getPlace()).isEqualTo(place);

        // Verify 호출 여부
        verify(elasticsearchTemplate, times(1)).search(any(CriteriaQuery.class), eq(EventDocument.class));
    }


    @Test
    @Transactional
    public void testSearchTransferableTickets() {
        // Given
        int page = 1;
        int size = 10;
        String keyword = "concert";
        LocalDate eventDate = LocalDate.of(2024, 1, 1);

        Pageable pageable = PageRequest.of(page - 1, size);
        Ticket ticket = TestUtil.getTicket(1L);  // Ticket 객체 초기화
        Page<Ticket> ticketPage = new PageImpl<>(Collections.singletonList(ticket), pageable, 1);

        // Mocking
        when(ticketService.getTicketsFromKeywords(eq(keyword), eq(eventDate), any())).thenReturn(ticketPage);

        // When
        Page<SearchTicketsResponse> result = searchService.searchTransferableTickets(page, size, keyword, eventDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);

        // Verify 호출 여부
        verify(ticketService, times(1)).getTicketsFromKeywords(keyword, eventDate, pageable);
    }


}