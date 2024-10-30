package io.nugulticket.payment.controller;


import io.nugulticket.config.payment.CommunicationPaymentUtil;
import io.nugulticket.config.payment.GenerateOrderIdUtil;
import io.nugulticket.payment.dto.request.PaymentRequest;
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

    // 성공적으로 결제 페이지가 끝났을 경우 호출
    @ResponseBody
    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayments(@RequestBody PaymentRequest paymentRequest) {
        System.out.println("들어왔어");

        JSONObject jsonObject = communicationPaymentUtil.preProcess(paymentRequest.getAmount(), paymentRequest.getOrderId(), paymentRequest.getUserId());
        JSONObject newjsonObject =communicationPaymentUtil.postProcess(paymentRequest.getAmount(), paymentRequest.getOrderId(), paymentRequest.getPaymentKey(), paymentRequest.getUserId());

        return ResponseEntity.status(200).body("{ \"i\" :  \"Test\" }");
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String Test(Model model) {
        model.addAttribute("userId", 1L);
        model.addAttribute("amount", 500);
        model.addAttribute("orderId", generateOrderIdUtil.generateOrderId());
        model.addAttribute("orderType", "RESERVE");
        model.addAttribute("orderName", "웃는 남자");
        model.addAttribute("email", "a@naver.com");

        return "/payment/checkout";
    }
}
