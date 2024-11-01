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

    @Query("""
        {
           "query": {
             "bool": {
               "should": [
                 {
                   "match": {
                     "title": {
                       "query": "?0",
                       "fuzziness": "AUTO"
                     }
                   }
                 },
                 {
                   "match": {
                     "place": {
                       "query": "?2",
                       "fuzziness": "AUTO"
                     }
                   }
                 },
                 {
                   "match": {
                     "category": {
                       "query": "?3",
                       "fuzziness": "AUTO"
                     }
                   }
                 },
                 {
                   "range": {
                     "startDate": {
                       "lte": "?1"
                     },
                     "endDate": {
                       "gte": "?1"
                     }
                   }
                 }
               ],
               "minimum_should_match": 1
             }
           },
           "sort": [
             {
               "startDate": {
                 "order": "asc"
               }
             },
             {
               "rating": {
                 "order": "desc"
               }
             }
           ]
         }
        """
    )
    Page<Event> findByKeywords(String keyword, LocalDate eventDate, String place, String category, Pageable pageable);
}
