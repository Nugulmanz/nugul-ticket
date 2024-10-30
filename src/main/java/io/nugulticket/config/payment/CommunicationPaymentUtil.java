package io.nugulticket.config.payment;

import net.minidev.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommunicationPaymentUtil {
    private final String PAYMENT_SERVER_URL = "http://localhost:8081";
    private final String PREPROCESS_PATH = "/preprocess";
    private final String POSTPROCESS_PATH = "/postprocess";

    private WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl(PAYMENT_SERVER_URL)
                .build();
    }

    private Map<String, Object> getBaseBody(int amount, String orderId, long userId) {
        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("orderId", orderId);
        body.put("userId", userId);

        return body;
    }

    // 1. API 호출하기
    public JSONObject preProcess(int amount, String orderId, long userId) {
        Map<String, Object> bodyMap = getBaseBody(amount, orderId, userId);

        return requestProcess(bodyMap, PREPROCESS_PATH);
    }

    // 1. API 호출하기
    public JSONObject postProcess(int amount, String orderId, String paymentId, long userId) {
        Map<String, Object> bodyMap = getBaseBody(amount, orderId, userId);
        bodyMap.put("paymentId", paymentId);

        return requestProcess(bodyMap, POSTPROCESS_PATH);
    }

    private JSONObject requestProcess(Map<String, Object> body, String uri) {
        WebClient webClient = getWebClient();

        return webClient.post()
                .uri(uri)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
    }
}
