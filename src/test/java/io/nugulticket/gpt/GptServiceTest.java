package io.nugulticket.gpt;

import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.email.service.RedisService;
import io.nugulticket.gpt.service.GptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Method;
import java.util.List;
import static org.mockito.Mockito.lenient;


@ExtendWith(MockitoExtension.class)
public class GptServiceTest {

    @InjectMocks
    private GptService gptService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RedisService redisService;

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private Long userId;
    private String userQuestion;

    @BeforeEach
    void setUp() {
        userId = 1L;
        userQuestion = "추천해줄 공연이 있나요?";
    }

    @Test
    void testAskChat_withValidUser_shouldReturnResponse() throws Exception {
        // given
        List<Object> mockKeywords = List.of("뮤지컬", "클래식 공연");
        when(redisService.getSearchKeywords(userId)).thenReturn(mockKeywords);

        String mockResponse = "{\"choices\":[{\"message\":{\"content\":\"추천드립니다!\"}}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        // restTemplate의 exchange() 호출 시 적절한 값이 넘어가도록 수정
        when(restTemplate.exchange(
                eq(apiUrl),  // apiUrl을 정확히 전달
                eq(HttpMethod.POST),
                any(HttpEntity.class),  // HttpEntity가 올바르게 전달되도록
                eq(String.class)
        )).thenReturn(responseEntity);

        // when
        String response = gptService.askChat(userId, userQuestion);

        // then
        assertEquals("추천드립니다!", response);
    }

    @Test
    void testAskChat_withNullUser_shouldReturnResponse() throws Exception {
        // given
        List<Object> mockKeywords = List.of("뮤지컬", "클래식 공연", "연극");

        String mockResponse = "{\"choices\":[{\"message\":{\"content\":\"추천드립니다!\"}}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(apiUrl), // apiUrl을 정확하게 매칭
                eq(HttpMethod.POST),
                any(HttpEntity.class), // HttpEntity는 어떤 객체든 허용하도록 설정
                eq(String.class)
        )).thenReturn(responseEntity);

        // when
        String response = gptService.askChat(null, userQuestion);

        // then
        assertEquals("추천드립니다!", response);
    }


    @Test
    void testAskChat_withBadRequest_shouldThrowApiException() {
        // given
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(),
                eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> gptService.askChat(userId, userQuestion));

        // Check if the error code is either GPT_API_BAD_REQUEST or GPT_API_RESPONSE_ERROR
        assertTrue(
                exception.getErrorCode() == ErrorStatus.GPT_API_BAD_REQUEST ||
                        exception.getErrorCode() == ErrorStatus.GPT_API_RESPONSE_ERROR
        );
    }



    @Test
    void testAskChat_withServerError_shouldThrowApiException() {
        // given
        lenient().when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(),
                eq(String.class)
        )).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> gptService.askChat(userId, userQuestion));

        // Check if the error code is either GPT_API_SERVER_ERROR or GPT_API_RESPONSE_ERROR
        assertTrue(
                exception.getErrorCode() == ErrorStatus.GPT_API_SERVER_ERROR ||
                        exception.getErrorCode() == ErrorStatus.GPT_API_RESPONSE_ERROR
        );
    }

    @Test
    void testExtractMessageContent_shouldReturnCorrectContent() throws Exception {
        // given
        String responseBody = "{\"choices\":[{\"message\":{\"content\":\"추천드립니다!\"}}]}";

        // 리플렉션을 사용하여 private 메서드 접근
        Method method = GptService.class.getDeclaredMethod("extractMessageContent", String.class);
        method.setAccessible(true);

        // when
        String result = (String) method.invoke(gptService, responseBody);

        // then
        assertEquals("추천드립니다!", result);
    }
}
