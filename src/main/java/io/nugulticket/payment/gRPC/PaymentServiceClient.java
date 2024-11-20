package io.nugulticket.payment.gRPC;

import com.example.payment.grpc.PaymentServiceGrpc;
import com.example.payment.grpc.PaymentServiceProto.PaymentRequest;
import com.example.payment.grpc.PaymentServiceProto.PaymentResponse;
import com.example.payment.grpc.PaymentServiceProto.UserDetails;
import com.example.payment.grpc.PaymentServiceProto.Address;
import com.example.payment.grpc.PaymentServiceProto.EventDetails;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceClient {

    @Value("${grpc.payment.url}")
    private String paymentServerUrl;

    private PaymentServiceGrpc.PaymentServiceBlockingStub paymentServiceStub;

    /**
     * gRPC 채널을 설정하고, 결제 서버의 gRPC 서비스를 호출할 수 있는 PaymentServiceBlockingStub 객체를 생성
     */
    @PostConstruct
    public void init() {
        // 호스트와 포트를 분리하여 설정
        String[] urlParts = paymentServerUrl.split(":");
        String host = urlParts[0];
        int port = Integer.parseInt(urlParts[1]);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .maxInboundMessageSize(100 * 1024 * 1024) // 메시지 최대 크기를 100MB로 설정
                .build();
        paymentServiceStub = PaymentServiceGrpc.newBlockingStub(channel);
    }

    /**
     * 결제 서버에 결제 정보를 요청하는 메서드
     * @param orderId
     * @param userId
     * @param userRole
     * @param email
     * @param events
     * @return 결제 서버 쪽에서 반환하는 결제정보
     */
    public PaymentResponse getPaymentInfo(String orderId, long userId, String userRole, String email, List<EventDetails> events) {
        UserDetails userDetails = UserDetails.newBuilder()
                .setUserRole(userRole)
                .setEmail(email)
                .setAddress(Address.newBuilder()
                        .setStreet("123 Main St")
                        .setCity("Metropolis")
                        .setState("CA")
                        .setZip("12345")
                        .build())
                .build();

        // EventDetails 리스트 생성
        events = createEvents(10000); // 예: 10000개의 이벤트 생성

        PaymentRequest paymentRequest = PaymentRequest.newBuilder()
                .setOrderId(orderId)
                .setUserId(userId)
                .setUser(userDetails)
                .addAllEvent(events)
                .build();

        // 결제 서버의 GetPaymentInfo 메서드 호출
        return paymentServiceStub.getPaymentInfo(paymentRequest);
    }

    /**
     * EventDetails 생성 메서드
     * @param count 생성횟수
     * @return 리스트 형태의 EventDetails
     */
    private List<EventDetails> createEvents(int count) {
        List<EventDetails> events = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            events.add(EventDetails.newBuilder()
                    .setEventId("event-" + i)
                    .setTitle("Event Title " + i)
                    .setCategory("Category " + (i % 5))
                    .setPlace("Venue " + i)
                    .build());
        }
        return events;
    }

}
