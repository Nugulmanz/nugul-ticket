package io.nugulticket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAiConfig {

    @Value("${OPENAI_API_KEY}")
    private String openAiKey;

    @Bean
    public RestTemplate openAiRestTemplate() {
        RestTemplate openAiRestTemplate = new RestTemplate();

        // 인터셉터 추가: Authorization 헤더를 모든 요청에 자동 추가
        openAiRestTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openAiKey);
            return execution.execute(request, body);
        });

        return openAiRestTemplate;
    }
}