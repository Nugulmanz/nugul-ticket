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
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SearchEventRankService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String EVENT_RANKING_KEY = "event:ranking";
    private final EventService eventService;

    /**
     * 공연 키워드를 추가하고 점수를 반환합니다.
     * 시간 가중치를 점수에 반영하고 TTL을 설정합니다.
     *
     * @param keyword 공연 키워드
     * @param eventId 공연 ID
     * @return 점수와 공연 정보가 포함된 Map
     */
    public Map<String, Object> addKeywordAndGetScore(String keyword, Long eventId) {
        Long storedEventId = getEventIdByName(keyword);
        if (storedEventId == null || !storedEventId.equals(eventId)) {
            throw new ApiException(ErrorStatus.EVENT_NOT_FOUND);
        }

        long currentTimeInSeconds = System.currentTimeMillis() / 1000;
        double timeWeight = (currentTimeInSeconds % 10000) * 0.0001;
        double incrementScore = 1 + timeWeight;

        Double score = redisTemplate.opsForZSet().incrementScore(EVENT_RANKING_KEY, keyword, incrementScore);

        redisTemplate.expire(EVENT_RANKING_KEY, 7, TimeUnit.DAYS);

        Event event = eventService.getEventFromId(eventId); // 이벤트 서비스에서 가져옴
        GetEventResponse getEventResponse = new GetEventResponse(event);

        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("eventId", eventId);
        result.put("eventTitle", keyword);
        result.put("eventDetails", getEventResponse);

        return result;
    }


    /**
     * 상위 공연을 반환합니다.
     *
     * @param count 반환할 공연 개수
     * @return 상위 공연 리스트
     */
    public List<String> getTopEvents(int count) {
        Set<Object> topEvents = redisTemplate.opsForZSet()
                .reverseRange(EVENT_RANKING_KEY, 0, count - 1);

        return topEvents != null
                ? topEvents.stream().map(Object::toString).toList()
                : List.of();
    }

    /**
     * 공연 제목으로 Event ID를 반환합니다.
     *
     * @param eventTitle 공연 제목
     * @return Event ID
     */
    public Long getEventIdByName(String eventTitle) {
        Object eventIdStr = redisTemplate.opsForHash().get("event:Id:Map", eventTitle);

        if (eventIdStr instanceof String) {
            return Long.valueOf((String) eventIdStr);
        }
        return null;
    }
}
