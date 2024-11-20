package io.nugulticket.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {
    /**
     * Http 요청을 수행하기 위한 내용을 담는 메서드
     *
     * @param builder RestTemplate을 구성할 Builder
     * @return Http관련 요청에 대한 정보가 담긴 RestTemplate 객체
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // RestTemplate 으로 외부 API 호출 시 일정 시간(5초) 지나도 응답이 없을 때
        // 무한 대기 상태 방지를 위해 강제 종료 설정
        return builder.setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }
}
