package io.nugulticket.payment.controller;

import io.nugulticket.common.utils.payment.CommunicationPaymentUtil;
import io.nugulticket.common.utils.payment.GenerateOrderIdUtil;
import io.nugulticket.config.SQSProtocol;
import io.nugulticket.payment.dto.request.PaymentRequest;
import io.nugulticket.payment.dto.request.RestRequest;
import io.nugulticket.payment.entity.Address;
import io.nugulticket.payment.entity.EventDetails;
import io.nugulticket.payment.entity.UserDetails;
import io.nugulticket.sns.service.SnsService;
import io.nugulticket.sqs.dto.SQSApprovePayment;
import io.nugulticket.sqs.dto.SQSPreOrder;
import io.nugulticket.ticket.dto.response.TicketNeedPaymentResponse;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final CommunicationPaymentUtil communicationPaymentUtil;
    private final GenerateOrderIdUtil generateOrderIdUtil;
    private final SnsService snsService;

    // 성공적으로 결제 페이지가 끝났을 경우 호출
    @ResponseBody
    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayments(@RequestBody PaymentRequest paymentRequest) {
        System.out.println("들어왔어");

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

    //결제 정보 요청하는 REST API
    @GetMapping("/rest/info")
    public ResponseEntity<JSONObject> getPaymentInfo (@RequestParam String orderId, @RequestParam long userId) {
        Address address = new Address("123 Main St", "Metropolis", "CA", "12345");

        // UserDetails 객체 생성
        UserDetails userDetails = new UserDetails(
                "USER_ROLE_ADMIN",
                "test@example.com",
                address
        );

        // EventDetails 리스트 생성
        List<EventDetails> events = createEvents(10000); // 10000개의 이벤트 생성

        // 결제 서버에 정보 요청
        RestRequest restRequest = new RestRequest(orderId, userId, userDetails, events);

        // REST API 호출
        JSONObject paymentInfo = communicationPaymentUtil.getPaymentInfo(restRequest);
        return ResponseEntity.ok(paymentInfo);
    }

    // EventDetails 리스트 생성 메서드
    private List<EventDetails> createEvents(int count) {
        List<EventDetails> events = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            events.add(new EventDetails(
                    "event-" + i,
                    "Event Title " + i,
                    "Category " + (i % 5),
                    "Venue " + i
            ));
        }
        return events;
    }


}