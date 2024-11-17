package io.nugulticket.common.utils.payment;

import io.nugulticket.payment.dto.request.PaymentRequest;
import io.nugulticket.payment.dto.request.RestRequest;
import io.nugulticket.payment.entity.Address;
import io.nugulticket.payment.entity.EventDetails;
import io.nugulticket.payment.entity.UserDetails;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommunicationPaymentUtil {
    @Value("${payment.url}")
    private String paymentServerUrl;
    private final String PREPROCESS_PATH = "/preprocess";
    private final String POSTPROCESS_PATH = "/confirm/payment";
    private final String GETINFO_PATH = "/info/payment";



    private WebClient getWebClient() { // localhost:8081
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

    // 결제 정보 요청 REST API
    public JSONObject getPaymentInfo(RestRequest restRequest) {
        Map<String, Object> bodyMap = createRequestBody(restRequest);

        return requestProcess(bodyMap, GETINFO_PATH);
    }

    // 결제 정보 요청 REST API
    private Map<String, Object> createRequestBody(RestRequest restRequest) {
        Map<String, Object> body = new HashMap<>();
        body.put("orderId", restRequest.getOrderId());
        body.put("userId", restRequest.getUserId());

        // UserDetails
        UserDetails userDetails = restRequest.getUserDetails();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userRole", userDetails.getUserRole());
        userMap.put("email", userDetails.getEmail());

        // Address
        Address address = userDetails.getAddress();
        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("street", address.getStreet());
        addressMap.put("city", address.getCity());
        addressMap.put("state", address.getState());
        addressMap.put("zip", address.getZip());
        userMap.put("address", addressMap);

        body.put("userDetails", userMap);

        // Events
        List<Map<String, Object>> events = new ArrayList<>();
        for (EventDetails event : restRequest.getEvents()) {
            Map<String, Object> eventMap = new HashMap<>();
            eventMap.put("eventId", event.getEventId());
            eventMap.put("title", event.getTitle());
            eventMap.put("category", event.getCategory());
            eventMap.put("place", event.getPlace());
            events.add(eventMap);
        }
        body.put("events", events);

        return body;
    }


    private Map<String, Object> getBaseBody(int amount, String orderId, long userId) {
        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("orderId", orderId);
        body.put("userId", userId);

        return body;
    }

    // 8081 가져와서 그 위에 패스를 넣어서 보내주는거
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
