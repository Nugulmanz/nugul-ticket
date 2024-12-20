package io.nugulticket.payment.gRPC;

import com.example.payment.grpc.PaymentServiceGrpc;
import com.example.payment.grpc.PaymentServiceProto.EventDetails;
import com.example.payment.grpc.PaymentServiceProto.PaymentRequest;
import com.example.payment.grpc.PaymentServiceProto.PaymentResponse;
import com.example.payment.grpc.PaymentServiceProto.UserDetails;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class PaymentServiceImpl extends PaymentServiceGrpc.PaymentServiceImplBase {

    private final PaymentServiceClient paymentClient;

    /**
     * 티켓 서버에서 제공하는 gRPC 서비스 구현체
     * 티켓 서버가 결제 서버에서 데이터를 가져와 사용자에게 반환할 때 로직을 수행
     * @param request
     * @param responseObserver
     */
    @Override
    public void getPaymentInfo(PaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {
        String orderId = request.getOrderId();
        long userId = request.getUserId();

        UserDetails userDetails = request.getUser();
        String userRole = userDetails.getUserRole();
        String email = userDetails.getEmail();
        List<EventDetails> events = request.getEventList();

        // 결제 서버에 결제 정보를 요청하는 로직
        PaymentResponse paymentResponse = paymentClient.getPaymentInfo(orderId, userId, userRole, email, events);

        // 클라이언트로 응답 전송
        responseObserver.onNext(paymentResponse);

        responseObserver.onCompleted();

    }
}
