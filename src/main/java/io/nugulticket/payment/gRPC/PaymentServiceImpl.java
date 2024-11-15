package io.nugulticket.payment.gRPC;

import com.example.payment.grpc.PaymentServiceGrpc;
import com.example.payment.grpc.PaymentServiceProto.PaymentRequest;
import com.example.payment.grpc.PaymentServiceProto.PaymentResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// 티켓 서버에서 제공하는 gRPC 서비스 구현체
// 다른 서비스가 티켓 서버에 요청을 보낼 때, TicketServiceImpl이 이 요청을 처리
// 티켓 서버가 결제 서버에서 데이터를 가져와 사용자에게 반환할 때 로직을 수행
@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl extends PaymentServiceGrpc.PaymentServiceImplBase {

    private final PaymentServiceClient paymentClient;

    @Override
    public void getPaymentInfo(PaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {
        String orderId = request.getOrderId();
        long userId = request.getUserId();
        // 결제 서버에 결제 정보를 요청하는 로직
        PaymentResponse paymentResponse = paymentClient.getPaymentInfo(orderId, userId);

        // 클라이언트로 응답 전송
        responseObserver.onNext(paymentResponse);

        responseObserver.onCompleted();

    }
}
