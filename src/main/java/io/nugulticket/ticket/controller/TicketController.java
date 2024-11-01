package io.nugulticket.ticket.controller;

import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.ApiResponse;
import io.nugulticket.common.utils.payment.GenerateOrderIdUtil;
import io.nugulticket.ticket.dto.createTicket.CreateTicketRequest;
import io.nugulticket.ticket.dto.createTicket.CreateTicketResponse;
import io.nugulticket.ticket.dto.refundTicket.RefundTicketResponse;
import io.nugulticket.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets/v1")
public class TicketController {

    private final TicketService ticketService;

    // orderId 생성해주는 유틸
    private final GenerateOrderIdUtil generateOrderIdUtil;

    @RequestMapping
    public ModelAndView createTicket(
                                     @RequestBody CreateTicketRequest reqDto,
                                     @AuthenticationPrincipal AuthUser authUser) {

        // ModelAndView 쓰는 이유는, html도 보내줄 수 있어서 씀

        CreateTicketResponse resDto = ticketService.createTicket(reqDto, authUser);

        ModelAndView mav = new ModelAndView();

        mav.setViewName("/payment/checkout");

        mav.addObject("userId", authUser.getId());
        mav.addObject("amount", resDto.getPrice());
        mav.addObject("orderId", generateOrderIdUtil.generateOrderId());
        mav.addObject("orderName", resDto.getTitle());
        mav.addObject("email", authUser.getEmail());
        mav.addObject("orderType", "reserved");


        return mav;
    }

    @PatchMapping("/{ticketId}/refund")
    public ApiResponse<RefundTicketResponse> refundTicket(@PathVariable Long ticketId,
                                                          @AuthenticationPrincipal AuthUser authUser) {
        RefundTicketResponse resDto = ticketService.refundTicket(ticketId, authUser);
        return ApiResponse.ok(resDto);
    }
}
