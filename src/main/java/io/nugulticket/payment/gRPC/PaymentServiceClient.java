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

// 결제 서버에 결제 정보를 요청할 때 사용하는 gRPC 클라이언트
// 이 클래스는 결제 서버의 gRPC 메서드(ProcessPayment)를 호출하여 결제 정보를 받아오고, 이 정보를 티켓 서버의 비즈니스 로직에 활용
@Service
@RequiredArgsConstructor
public class PaymentServiceClient {

    @Value("${grpc.payment.url}")
    private String paymentServerUrl;

    private PaymentServiceGrpc.PaymentServiceBlockingStub paymentServiceStub;

    // gRPC 채널을 설정하고, 결제 서버의 gRPC 서비스를 호출할 수 있는 PaymentServiceBlockingStub 객체를 생성
    @PostConstruct
    public void init () {
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

    // 결제 서버에 결제 정보를 요청하는 메서드
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

    // EventDetails 생성 메서드
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
