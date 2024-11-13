package io.nugulticket.search;

import io.nugulticket.search.service.SearchRankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SearchRankServiceTest {

    @InjectMocks
    private SearchRankService searchRankService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    private static final String SEARCH_RANKING_KEY = "searchRanking";

    @BeforeEach
    void setUp() {
        // RedisTemplate의 ZSetOperations를 Mock 객체로 설정
        given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
    }

    @Test
    @DisplayName("키워드 추가 및 점수 반환")
    void addKeywordAndGetScore() {
        // given
        String keyword = "exampleKeyword";

        given(zSetOperations.incrementScore(SEARCH_RANKING_KEY, keyword, 1)).willReturn(1.0);
        given(zSetOperations.score(SEARCH_RANKING_KEY, keyword)).willReturn(1.0);

        // when
        Double score = searchRankService.addKeywordAndGetScore(keyword);

        // then
        assertNotNull(score);
        assertEquals(1.0, score);
        verify(zSetOperations, times(1)).incrementScore(SEARCH_RANKING_KEY, keyword, 1);
        verify(zSetOperations, times(1)).score(SEARCH_RANKING_KEY, keyword);
    }

    @Test
    @DisplayName("상위 키워드 조회")
    void getTopKeywords() {
        // given
        int count = 3;
        Set<Object> mockTopKeywords = new LinkedHashSet<>(List.of("keyword1", "keyword2", "keyword3"));

        given(zSetOperations.reverseRange(SEARCH_RANKING_KEY, 0, count - 1)).willReturn(mockTopKeywords);

        // when
        List<String> topKeywords = searchRankService.getTopKeywords(count);

        // then
        assertNotNull(topKeywords);
        assertEquals(3, topKeywords.size());
        assertEquals(List.of("keyword1", "keyword2", "keyword3"), topKeywords);
        verify(zSetOperations, times(1)).reverseRange(SEARCH_RANKING_KEY, 0, count - 1);
    }
}