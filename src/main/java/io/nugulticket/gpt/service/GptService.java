package io.nugulticket.gpt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.email.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GptService {

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Qualifier("openAiRestTemplate")
    private final RestTemplate restTemplate;
    private final RedisService redisService;

    /**
     * 사용자의 질문을 GPT API에 전달하고 답변을 반환하는 메서드
     *
     * @param userId       사용자 ID (인증된 사용자라면 Redis에서 데이터를 기반으로 추천 제공)
     * @param userQuestion 사용자가 GPT에게 질문한 내용
     * @return GPT로부터 받은 답변 문자열
     */
    public String askChat(Long userId, String userQuestion) {
        List<Object> searchKeywords = List.of();

        if (userId != null) {
            searchKeywords = redisService.getSearchKeywords(userId);
        } else {
            searchKeywords = List.of("뮤지컬", "클래식 공연", "연극");
        }

        StringBuilder context = new StringBuilder("사용자가 관심 있는 공연 정보:\n");

        if (!searchKeywords.isEmpty()) {
            context.append("최근 검색 키워드: ").append(searchKeywords).append("\n");
        }
        context.append("질문: ").append(userQuestion);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "당신은 공연 추천 전문 챗봇입니다."),
                Map.of("role", "user", "content", context.toString())
        ));
        requestBody.put("max_tokens", 200);
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return extractMessageContent(response.getBody());

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ApiException(ErrorStatus.GPT_API_BAD_REQUEST);
            }
            throw new ApiException(ErrorStatus.GENERAL_SERVER_ERROR);
        } catch (HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                throw new ApiException(ErrorStatus.GPT_API_SERVER_ERROR);
            }
            throw new ApiException(ErrorStatus.GENERAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new ApiException(ErrorStatus.GPT_API_RESPONSE_ERROR);
        }
    }

    /**
     * GPT API 응답에서 메시지 내용을 추출하는 메서드
     *
     * @param responseBody GPT API로부터 받은 JSON 응답 문자열
     * @return GPT 응답 메시지 중 'content' 필드 값
     */
    private String extractMessageContent(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            return rootNode
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        } catch (Exception e) {
            throw new ApiException(ErrorStatus.GPT_API_RESPONSE_ERROR);
        }
    }
}
