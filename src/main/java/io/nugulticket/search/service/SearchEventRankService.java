package io.nugulticket.search.service;

import io.nugulticket.event.dto.getEvent.GetEventResponse;
import io.nugulticket.event.entity.Event;
import io.nugulticket.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SearchEventRankService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String EVENT_RANKING_KEY = "eventRanking";
    private final EventService eventService;

    public Map<String, Object> addKeywordAndGetScore(String keyword) {
        // 공연 이름으로 eventId 조회
        Long eventId = getEventIdByName(keyword);

        if (eventId == null) {
            throw new RuntimeException("해당 공연을 찾을 수 없습니다.");
        }

        // 검색 키워드 점수 증가
        Double score = redisTemplate.opsForZSet().incrementScore(EVENT_RANKING_KEY, keyword, 1);
        redisTemplate.opsForHash().put("eventIdMap", keyword, eventId);

        // eventId로 데이터베이스에서 이벤트 조회
        Event event = eventService.getEventFromId(eventId);
        GetEventResponse getEventResponse = new GetEventResponse(event);

        // 결과 반환
        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("eventId", eventId);
        result.put("getEventResponse", getEventResponse);

        return result;
    }

    public List<String> getTopEvents(int count) {
        Set<Object> topEvents = redisTemplate.opsForZSet()
                .reverseRange(EVENT_RANKING_KEY, 0, count - 1);

        return topEvents.stream()
                .map(Object::toString)
                .toList();
    }

    // 공연 이름으로 eventId 조회
    public Long getEventIdByName(String eventTitle) {
        return (Long) redisTemplate.opsForHash().get("eventId", eventTitle);
    }
}
