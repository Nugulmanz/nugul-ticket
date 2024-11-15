package io.nugulticket.search;

import io.nugulticket.search.dto.searchTickets.SearchTicketsResponse;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {
    @InjectMocks
    private SearchService searchService;
    @Mock
    private TicketService ticketService;

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