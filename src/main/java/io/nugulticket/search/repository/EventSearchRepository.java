package io.nugulticket.search.repository;

import io.nugulticket.event.entity.Event;
import io.nugulticket.search.entity.EventDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EventSearchRepository extends ElasticsearchRepository<EventDocument, Long> {

    @Query("{\"bool\": {\"must\": [{\"match\": {\"title\": \"?0\"}}, " +
            "{\"range\": {\"startDate\": {\"lte\": \"?1\"}, \"endDate\": {\"gte\": \"?1\"}}}]}}")
    Page<Event> findByKeywords(String keyword, LocalDate eventDate, String place, String category, Pageable pageable);
}
