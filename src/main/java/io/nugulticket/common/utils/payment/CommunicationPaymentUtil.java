package io.nugulticket.common.utils.payment;

import io.nugulticket.payment.dto.request.PaymentRequest;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommunicationPaymentUtil {
    @Value("${payment.url}")
    private String paymentServerUrl;
    private final String PREPROCESS_PATH = "/preprocess";
    private final String POSTPROCESS_PATH = "/confirm/payment";

    private WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl(paymentServerUrl)
                .build();
    }



    // 1. API 호출하기
    public JSONObject preProcess(PaymentRequest paymentRequest) {
        Map<String, Object> bodyMap = getBaseBody(paymentRequest.getAmount(), paymentRequest.getOrderId(), paymentRequest.getUserId());

        return requestProcess(bodyMap, PREPROCESS_PATH);
    }

    // 1. API 호출하기
    public JSONObject postProcess(PaymentRequest paymentRequest) {
        Map<String, Object> bodyMap = getBaseBody(paymentRequest.getAmount(), paymentRequest.getOrderId(), paymentRequest.getUserId());
        bodyMap.put("paymentKey", paymentRequest.getPaymentKey());

        return requestProcess(bodyMap, POSTPROCESS_PATH);
    }

    private Map<String, Object> getBaseBody(int amount, String orderId, long userId) {
        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("orderId", orderId);
        body.put("userId", userId);

        return body;
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
