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

        if (authUser.getId() != request.getUserId()) {
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }

        PaymentResponse response = paymentServiceClient.getPaymentInfo(request.getOrderId(), request.getUserId());

        PaymentInfoResponse paymentInfoResponse = new PaymentInfoResponse(
                response.getOrderId(),
                response.getUserId(),
                response.getAmount(),
                response.getSuccess(),
                response.getMessage()
        );

        return ResponseEntity.ok(paymentInfoResponse);
    }
}
