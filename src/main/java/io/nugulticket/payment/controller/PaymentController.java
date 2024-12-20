package io.nugulticket.payment.controller;

import io.nugulticket.common.utils.payment.GenerateOrderIdUtil;
import io.nugulticket.config.SQSProtocol;
import io.nugulticket.payment.dto.request.PaymentRequest;
import io.nugulticket.sns.service.SnsService;
import io.nugulticket.sqs.dto.SQSApprovePayment;
import io.nugulticket.sqs.dto.SQSPreOrder;
import io.nugulticket.ticket.dto.response.TicketNeedPaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final GenerateOrderIdUtil generateOrderIdUtil;
    private final SnsService snsService;

    // 성공적으로 결제 페이지가 끝났을 경우 호출
    @ResponseBody
    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayments(@RequestBody PaymentRequest paymentRequest) {
        log.info("confirm payments");

        // 최종 결제 승인
        SQSApprovePayment sqsApprovePayment = new SQSApprovePayment(
                SQSProtocol.TYPE_APPROVE_PAYMENT,
                paymentRequest.getTicketId(),
                paymentRequest.getPaymentKey(),
                paymentRequest.getOrderId(),
                paymentRequest.getUserId(),
                paymentRequest.getAmount(),
                0
        );
        PublishResponse response = snsService.publishToPaymentTopic(sqsApprovePayment.toSNSAttributes());
        System.out.println(response.messageId());

        return ResponseEntity.status(200).body("Success");
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
        SQSPreOrder preOrderDto = new SQSPreOrder(SQSProtocol.TYPE_PRE_ORDER,
                ticketNeedPaymentResponse.getOrderName(),
                ticketNeedPaymentResponse.getAmount());

        snsService.publishToPaymentTopic(preOrderDto.toSNSAttributes());

        model.addAllAttributes(ticketNeedPaymentResponse.toMap());

        return "/payment/checkout";
    }


}