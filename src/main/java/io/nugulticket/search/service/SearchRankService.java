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
    private static final String SEARCH_RANKING_KEY = "searchRanking";

    public Double addKeywordAndGetScore(String keyword) {

        redisTemplate.opsForZSet().incrementScore(SEARCH_RANKING_KEY, keyword, 1);
        return redisTemplate.opsForZSet().score(SEARCH_RANKING_KEY, keyword);
    }


    public List<String> getTopKeywords(int count) {
        Set<Object> topKeywords = redisTemplate.opsForZSet()
                .reverseRange(SEARCH_RANKING_KEY, 0, count - 1);

        return topKeywords.stream()
                .map(Object::toString)
                .toList();
    }
}
