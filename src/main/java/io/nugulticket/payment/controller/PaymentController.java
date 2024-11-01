package io.nugulticket.payment.controller;


import io.nugulticket.common.utils.payment.CommunicationPaymentUtil;
import io.nugulticket.common.utils.payment.GenerateOrderIdUtil;
import io.nugulticket.payment.PaymentService;
import io.nugulticket.payment.dto.request.PaymentRequest;
import io.nugulticket.ticket.dto.response.TicketNeedPaymentResponse;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final CommunicationPaymentUtil communicationPaymentUtil;
    private final GenerateOrderIdUtil generateOrderIdUtil;
    private final PaymentService paymentService;

    // 성공적으로 결제 페이지가 끝났을 경우 호출
    @ResponseBody
    @PostMapping("/confirm")
    public ResponseEntity<JSONObject> confirmPayments(@RequestBody PaymentRequest paymentRequest) {
        System.out.println("들어왔어");

        // 최종 결제 승인
        JSONObject newjsonObject =communicationPaymentUtil.postProcess(paymentRequest);

        int statusCode = newjsonObject.containsKey("error") ? 400 : 200;

        if(statusCode == 200) {
            paymentService.successfully(paymentRequest.getOrderType(), paymentRequest.getTicketId(), paymentRequest.getUserId());
        } else {
            paymentService.fail(paymentRequest.getOrderType(), paymentRequest.getTicketId(), paymentRequest.getUserId());
        }

        return ResponseEntity.status(statusCode).body(newjsonObject);
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String Test(Model model) {
        TicketNeedPaymentResponse ticketNeedPaymentResponse = new TicketNeedPaymentResponse(
                1L,
                1L,
                "RESERVE",
                "웃는 남자",
                "a@naver.com",
                generateOrderIdUtil.generateOrderId(),
                500
        );

        model.addAllAttributes(ticketNeedPaymentResponse.toMap());

        return "/payment/checkout";
    }
}