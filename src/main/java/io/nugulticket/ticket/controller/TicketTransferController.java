package io.nugulticket.ticket.controller;

import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.ApiResponse;
import io.nugulticket.ticket.dto.response.MyTransferTicketsResponse;
import io.nugulticket.ticket.dto.response.TicketNeedPaymentResponse;
import io.nugulticket.ticket.dto.response.TicketTransferCancelResponse;
import io.nugulticket.ticket.dto.response.TicketTransferResponse;
import io.nugulticket.ticket.service.TicketTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 양도 받은 티켓, 티켓 양도하기 / 신청과 같이 티켓 양도 기능 관련 컨트롤러
 * AuthUser 클래스가 추가될 경우 내용 추가 예정
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/tickets/v1")
public class TicketTransferController {

    private final TicketTransferService ticketTransferService;


    @RequestMapping(value = "/{ticketId}/transfer/apply", method = RequestMethod.POST)
    public String applyTransfer(Model model, @AuthenticationPrincipal AuthUser user, @PathVariable("ticketId") Long ticketId) {

        TicketNeedPaymentResponse ticketNeedPaymentResponse = ticketTransferService.applyTransferBeforePayment(user, ticketId);
        model.addAllAttributes(ticketNeedPaymentResponse.toMap());

        return "/payment/checkout";
    }

    @ResponseBody
    @RequestMapping(value = "/{ticketId}", method = RequestMethod.DELETE)
    public ApiResponse<TicketTransferCancelResponse> cancelTransfer(@AuthenticationPrincipal AuthUser user, @PathVariable("ticketId") Long ticketId) {
        return ApiResponse.ok(ticketTransferService.cancelTransfer(user, ticketId));
    }

    @ResponseBody
    @RequestMapping(value = "/{ticketId}/transfer", method = RequestMethod.POST)
    public ApiResponse<TicketTransferResponse> createTransfer(@AuthenticationPrincipal AuthUser user, @PathVariable("ticketId") Long ticketId) {
        return ApiResponse.ok(ticketTransferService.ticketTransfer(user, ticketId));
    }

    @ResponseBody
    @RequestMapping(value = "/transfer", method = RequestMethod.GET)
    public ApiResponse<MyTransferTicketsResponse> getMyTransferTickets(@AuthenticationPrincipal AuthUser user) {
        return ApiResponse.ok(ticketTransferService.getMyTransferTicket(user));
    }
}
