package io.nugulticket.search.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class SearchRankService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String SEARCH_RANKING_KEY = "search:ranking";

    /**
     * 키워드를 추가하고 점수를 반환합니다.
     * 시간 가중치를 점수에 반영하고 TTL을 설정합니다.
     *
     * @param keyword 추가할 키워드
     * @return 점수
     */
    public Double addKeywordAndGetScore(String keyword) {
        long currentTimeInSeconds = System.currentTimeMillis() / 1000;

        double timeWeight = (currentTimeInSeconds % 10000) * 0.0001;
        double incrementScore = 1 + timeWeight;

        redisTemplate.opsForZSet().incrementScore(SEARCH_RANKING_KEY, keyword, incrementScore);

        redisTemplate.expire(SEARCH_RANKING_KEY, 7, TimeUnit.DAYS);

        return redisTemplate.opsForZSet().score(SEARCH_RANKING_KEY, keyword);
    }


    /**
     * 상위 검색어를 반환합니다.
     *
     * @param count 반환할 검색어 개수
     * @return 상위 검색어 리스트
     */
    public List<String> getTopKeywords(int count) {
        Set<Object> topKeywords = redisTemplate.opsForZSet()
                .reverseRange(SEARCH_RANKING_KEY, 0, count - 1);

        return topKeywords != null
                ? topKeywords.stream().map(Object::toString).toList()
                : List.of();
    }
}
