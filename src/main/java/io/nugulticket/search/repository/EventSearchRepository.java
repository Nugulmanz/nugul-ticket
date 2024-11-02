package io.nugulticket.search.repository;

import io.nugulticket.search.entity.EventDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventSearchRepository extends ElasticsearchRepository<EventDocument, Long> {

}
