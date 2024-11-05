package io.nugulticket.search.service;

import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
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

    public Map<String, Object> addKeywordAndGetScore(String keyword, Long eventId) {

        Long storedEventId = getEventIdByName(keyword);
        if (storedEventId == null || !storedEventId.equals(eventId)) {
            throw new ApiException(ErrorStatus.EVENT_NOT_FOUND);
        }

        Double score = redisTemplate.opsForZSet().incrementScore(EVENT_RANKING_KEY, keyword, 1);
        redisTemplate.opsForHash().put("eventIdMap", keyword, eventId.toString());

        Event event = eventService.getEventFromId(eventId);
        GetEventResponse getEventResponse = new GetEventResponse(event);

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

    public Long getEventIdByName(String eventTitle) {

        Object eventIdStr = redisTemplate.opsForHash().get("eventIdMap", eventTitle);

        if (eventIdStr instanceof Long) {
            return (Long) eventIdStr;
        } else if (eventIdStr instanceof String) {
            return Long.valueOf((String) eventIdStr);
        }
        return null;
    }
}
