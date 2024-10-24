package io.nugulticket.search.service;

import io.nugulticket.event.entity.Event;
import io.nugulticket.event.repository.EventRepository;
import io.nugulticket.search.dto.searchEvents.SearchEventsResponse;
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

    private final EventRepository eventRepository;

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
        Page<Event> events = eventRepository.findByKeywords(keyword, eventDate, place, category, pageable);
        return events.map(SearchEventsResponse::of);
    }
}
