package io.nugulticket.ticket.controller;

import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.ApiResponse;
import io.nugulticket.ticket.dto.response.TicketCancelledResponse;
import io.nugulticket.ticket.dto.response.TicketCompletedResponse;
import io.nugulticket.ticket.dto.response.TicketReservedResponse;
import io.nugulticket.ticket.service.TicketInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketInquiryController {
    private final TicketInquiryService ticketInquiryService;

    @GetMapping("/reserved")
    public ApiResponse<List<TicketReservedResponse>> reservedTicket(
            @AuthenticationPrincipal AuthUser authUser) {

        List<TicketReservedResponse> response = ticketInquiryService.reservedTicket(authUser);

        return ApiResponse.ok(response);
    }

    @GetMapping("/cancelled")
    public ApiResponse<List<TicketCancelledResponse>> cancelledTicket(
            @AuthenticationPrincipal AuthUser authUser) {

        List<TicketCancelledResponse> response = ticketInquiryService.cancelledTicket(authUser);

        return ApiResponse.ok(response);
    }

    @GetMapping("/completed")
    public ApiResponse<List<TicketCompletedResponse>> completedTicket(
            @AuthenticationPrincipal AuthUser authUser) {

        List<TicketCompletedResponse> response = ticketInquiryService.completedTicket(authUser);

        return ApiResponse.ok(response);
    }

}

