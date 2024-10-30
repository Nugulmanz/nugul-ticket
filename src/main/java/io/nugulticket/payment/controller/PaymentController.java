package io.nugulticket.payment.controller;


import io.nugulticket.config.CommunicationPaymentUtil;
import io.nugulticket.payment.dto.request.PaymentRequest;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final CommunicationPaymentUtil communicationPaymentUtil;

    // 성공적으로 결제 페이지가 끝났을 경우 호출
    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayments(@RequestBody PaymentRequest paymentRequest) {
        System.out.println("들어왔어");

        JSONObject jsonObject = communicationPaymentUtil.preProcess(paymentRequest.getAmount(), paymentRequest.getOrderId());
        JSONObject newjsonObject =communicationPaymentUtil.postProcess(paymentRequest.getAmount(), paymentRequest.getOrderId(), paymentRequest.getPaymentKey());

        return ResponseEntity.status(200).body("{ \"i\" :  \"Test\" }");
    }

}
