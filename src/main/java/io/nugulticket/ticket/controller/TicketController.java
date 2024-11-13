package io.nugulticket.ticket.controller;

import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.ApiResponse;
import io.nugulticket.common.utils.payment.CommunicationPaymentUtil;
import io.nugulticket.common.utils.payment.GenerateOrderIdUtil;
import io.nugulticket.config.SQSProtocol;
import io.nugulticket.payment.dto.request.PaymentRequest;
import io.nugulticket.sns.service.SnsService;
import io.nugulticket.sqs.dto.SQSPreOrder;
import io.nugulticket.ticket.dto.createTicket.CreateTicketRequest;
import io.nugulticket.ticket.dto.refundTicket.RefundTicketResponse;
import io.nugulticket.ticket.dto.response.TicketNeedPaymentResponse;
import io.nugulticket.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets/v1")
public class TicketController {

    private final TicketService ticketService;
    private final SnsService snsService;
    private final CommunicationPaymentUtil communicationPaymentUtil;

    // orderId 생성해주는 유틸
    private final GenerateOrderIdUtil generateOrderIdUtil;

    @RequestMapping
    public ModelAndView createTicket(
                                     @RequestBody CreateTicketRequest reqDto,
                                     @AuthenticationPrincipal AuthUser authUser) {
        TicketNeedPaymentResponse resDto = ticketService.createTicket(reqDto, authUser);
        SQSPreOrder preOrderDto = new SQSPreOrder(SQSProtocol.TYPE_PRE_ORDER,
                resDto.getOrderName(),
                resDto.getAmount());

        snsService.publishToPaymentTopic(preOrderDto.toSNSAttributes());

        // ModelAndView 쓰는 이유는, html도 보내줄 수 있어서 씀
        ModelAndView mav = new ModelAndView();

        mav.setViewName("/payment/checkout");

        // 페이먼츠 서버에 전송(처음 결제 정보 저장)
        JSONObject jsonObject = communicationPaymentUtil.preProcess(PaymentRequest.of(resDto));

        // 이렇게 넣으면 한방에 다 들어감
        mav.addAllObjects(resDto.toMap());

        return mav;
    }

    @PatchMapping("/{ticketId}/refund")
    public ApiResponse<RefundTicketResponse> refundTicket(@PathVariable Long ticketId,
                                                          @AuthenticationPrincipal AuthUser authUser) {
        RefundTicketResponse resDto = ticketService.refundTicket(ticketId, authUser);
        return ApiResponse.ok(resDto);
    }
}
