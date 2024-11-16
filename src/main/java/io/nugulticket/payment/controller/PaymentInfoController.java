package io.nugulticket.payment.controller;

import com.example.payment.grpc.PaymentServiceProto.PaymentResponse;
import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.payment.dto.request.PaymentInfoRequest;
import io.nugulticket.payment.dto.response.PaymentInfoResponse;
import io.nugulticket.payment.gRPC.PaymentServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentInfoController {

    private final PaymentServiceClient paymentServiceClient;

    // 결제 정보를 조회하는 메서드 (내부적으로 gRPC 통신)
    @GetMapping("/info")
    public ResponseEntity<PaymentInfoResponse> getPaymentInfo (
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody PaymentInfoRequest request) {

        // 해당 유저가 입력한 userId와 일치하지 않은 경우
        if (authUser.getId() != request.getUserId()) {
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }

        // largeStrings 생성
        List<String> largeStrings = generateLargeStrings();

        // gRPC 호출
        PaymentResponse response = paymentServiceClient.getPaymentInfo(
                request.getOrderId(),
                request.getUserId(),
                String.valueOf(authUser.getUserRole()),
                authUser.getEmail(),
                largeStrings
        );

        PaymentInfoResponse paymentInfoResponse = new PaymentInfoResponse(
                response.getOrderId(),
                response.getUserId(),
                response.getAmount(),
                response.getSuccess(),
                response.getMessage()
        );

        return ResponseEntity.ok(paymentInfoResponse);
    }

    // largeStrings 생성 메서드
    private List<String> generateLargeStrings() {
        String longString = "a".repeat(65350); // 65350자 문자열 생성
        List<String> largeStrings = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {        // 1000개의 문자열 추가
            largeStrings.add(longString);
        }
        return largeStrings;
    }
}
