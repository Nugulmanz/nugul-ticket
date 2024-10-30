package io.nugulticket.search.controller;

import io.nugulticket.common.apipayload.ApiResponse;
import io.nugulticket.search.service.SearchRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search_rank/v1")
@RequiredArgsConstructor
public class SearchRankController {

    private final SearchRankService searchRankService;

    @PostMapping("/keywords")
    public ApiResponse<Double> addKeyword(@RequestParam String keyword) {
        double currentScore = searchRankService.addKeywordAndGetScore(keyword);
        return ApiResponse.ok(currentScore);
    }

    // 상위 검색어 조회
    @GetMapping("/keywords/top")
    public ApiResponse<List<String>> getTopKeywords(@RequestParam(defaultValue = "10") int count) {
        return ApiResponse.ok(searchRankService.getTopKeywords(count));
    }
}

