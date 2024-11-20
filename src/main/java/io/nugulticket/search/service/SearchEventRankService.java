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
    private static final String EVENT_RANKING_KEY = "event:ranking";
    private final EventService eventService;

    /**
     * keyword를 삽입하고 해당 keyword에 해당하는 Score값을 반환하는 메서드
     *
     * @param keyword 삽입할 keyword
     * @param eventId 삽입할 공연 Id
     * @return Score / EventId / 공연 Response 객체 정보가 담긴 Map 객체
     */
    public Map<String, Object> addKeywordAndGetScore(String keyword, Long eventId) {

        Long storedEventId = getEventIdByName(keyword);
        if (storedEventId == null || !storedEventId.equals(eventId)) {
            throw new ApiException(ErrorStatus.EVENT_NOT_FOUND);
        }

        Double score = redisTemplate.opsForZSet().incrementScore(EVENT_RANKING_KEY, keyword, 1);
        redisTemplate.opsForHash().put("event:Id:Map", keyword, eventId.toString());

        Event event = eventService.getEventFromId(eventId);
        GetEventResponse getEventResponse = new GetEventResponse(event);

        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("eventId", eventId);
        result.put("getEventResponse", getEventResponse);

        return result;
    }

    /**
     * 1 ~ count 등수까지 조회하여 반환하는 메서드
     *
     * @param count 최대 등수
     * @return 1 ~ count 등수까지의 공연 정보가 담긴 List 객체
     */
    public List<String> getTopEvents(int count) {
        Set<Object> topEvents = redisTemplate.opsForZSet()
                .reverseRange(EVENT_RANKING_KEY, 0, count - 1);

        return topEvents.stream()
                .map(Object::toString)
                .toList();
    }

    /**
     * 해당 공연 타이틀에 해당하는 event Id를 반환하는 메서드
     *
     * @param eventTitle 조회할 공연 타이틀
     * @return 해당 공연 타이틀에 해당하는 eventID
     */
    public Long getEventIdByName(String eventTitle) {

        Object eventIdStr = redisTemplate.opsForHash().get("event:Id:Map", eventTitle);

        if (eventIdStr instanceof Long) {
            return (Long) eventIdStr;
        } else if (eventIdStr instanceof String) {
            return Long.valueOf((String) eventIdStr);
        }
        return null;
    }
}
