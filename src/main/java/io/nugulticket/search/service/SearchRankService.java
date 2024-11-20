package io.nugulticket.search.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class SearchRankService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String SEARCH_RANKING_KEY = "search:ranking";

    /**
     * keyword를 삽입하고 해당 keyword에 해당하는 Score값을 반환하는 메서드
     *
     * @param keyword 삽입할 keyword
     * @return 해당 keyword에 해당하는 Score
     */
    public Double addKeywordAndGetScore(String keyword) {

        redisTemplate.opsForZSet().incrementScore(SEARCH_RANKING_KEY, keyword, 1);
        return redisTemplate.opsForZSet().score(SEARCH_RANKING_KEY, keyword);
    }

    /**
     * 1 ~ count 등수까지 조회하여 반환하는 메서드
     *
     * @param count 최대 등수
     * @return 1 ~ count 등수까지의 공연 정보가 담긴 List 객체
     */
    public List<String> getTopKeywords(int count) {
        Set<Object> topKeywords = redisTemplate.opsForZSet()
                .reverseRange(SEARCH_RANKING_KEY, 0, count - 1);

        return topKeywords.stream()
                .map(Object::toString)
                .toList();
    }
}
