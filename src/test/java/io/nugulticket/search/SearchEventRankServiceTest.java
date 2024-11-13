package io.nugulticket.search;

import io.nugulticket.event.service.EventService;
import io.nugulticket.search.service.SearchEventRankService;
import io.nugulticket.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchEventRankServiceTest {

    @InjectMocks
    private SearchEventRankService searchEventRankService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private EventService eventService;

    private static final String EVENT_RANKING_KEY = "eventRanking";
    private static final String EVENT_ID_MAP_KEY = "eventIdMap";


    @Test
    @DisplayName("이벤트 키워드 추가 및 점수 반환")
    void addKeywordAndGetScore() {
        // given
        String keyword = "concert";
        Long eventId = 1L;
        Double expectedScore = 1.0;

        // 필요한 mocking 설정
        given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(searchEventRankService.getEventIdByName(keyword)).willReturn(eventId);
        given(zSetOperations.incrementScore(EVENT_RANKING_KEY, keyword, 1)).willReturn(expectedScore);
        given(eventService.getEventFromId(eventId)).willReturn(TestUtil.getEvent(1L));
        doNothing().when(hashOperations).put(EVENT_ID_MAP_KEY, keyword, eventId.toString());

        // when
        Map<String, Object> result = searchEventRankService.addKeywordAndGetScore(keyword, eventId);

        // then
        assertNotNull(result);
        assertEquals(expectedScore, result.get("score"));
        assertEquals(eventId, result.get("eventId"));
        assertNotNull(result.get("getEventResponse"));
        verify(zSetOperations, times(1)).incrementScore(EVENT_RANKING_KEY, keyword, 1);
        verify(hashOperations, times(1)).put(EVENT_ID_MAP_KEY, keyword, eventId.toString());
    }

    @Test
    @DisplayName("상위 이벤트 키워드 조회")
    void getTopEvents() {
        int count = 3;
        Set<Object> mockTopEvents = new LinkedHashSet<>(List.of("concert1", "concert2", "concert3"));

        // 필요한 mocking 설정
        given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
        given(zSetOperations.reverseRange(EVENT_RANKING_KEY, 0, count - 1)).willReturn(mockTopEvents);

        List<String> topEvents = searchEventRankService.getTopEvents(count);

        assertNotNull(topEvents);
        assertEquals(3, topEvents.size());
        assertEquals(List.of("concert1", "concert2", "concert3"), topEvents);
        verify(zSetOperations, times(1)).reverseRange(EVENT_RANKING_KEY, 0, count - 1);
    }

    @Test
    @DisplayName("키워드로 eventId 조회")
    void getEventIdByName() {
        // given
        String keyword = "concert";
        Long eventId = 1L;

        // 필요한 mocking 설정
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(hashOperations.get(EVENT_ID_MAP_KEY, keyword)).willReturn(eventId);

        // when
        Long result = searchEventRankService.getEventIdByName(keyword);

        // then
        assertNotNull(result);
        assertEquals(eventId, result);
        verify(hashOperations, times(1)).get(EVENT_ID_MAP_KEY, keyword);
    }
}