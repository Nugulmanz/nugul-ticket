package io.nugulticket.ticket.controller;

import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.ApiResponse;
import io.nugulticket.ticket.dto.response.MyTransferTicketsResponse;
import io.nugulticket.ticket.dto.response.TicketTransferApplyResponse;
import io.nugulticket.ticket.dto.response.TicketTransferCancelResponse;
import io.nugulticket.ticket.dto.response.TicketTransferResponse;
import io.nugulticket.ticket.service.TicketTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 양도 받은 티켓, 티켓 양도하기 / 신청과 같이 티켓 양도 기능 관련 컨트롤러
 * AuthUser 클래스가 추가될 경우 내용 추가 예정
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets/v1")
public class TicketTransferController {

    private final TicketTransferService ticketTransferService;

    @PostMapping("/{ticketId}/transfer/apply")
    public ApiResponse<TicketTransferApplyResponse> applyTransfer(@AuthenticationPrincipal AuthUser user, @PathVariable("ticketId") Long ticketId) {
        return ApiResponse.ok(ticketTransferService.applyTransfer(user, ticketId));
    }

    @DeleteMapping("/{ticketId}")
    public ApiResponse<TicketTransferCancelResponse> cancelTransfer(@AuthenticationPrincipal AuthUser user, @PathVariable("ticketId") Long ticketId) {
        return ApiResponse.ok(ticketTransferService.cancelTransfer(user, ticketId));
    }

    @PostMapping("/{ticketId}/transfer")
    public ApiResponse<TicketTransferResponse> createTransfer(@AuthenticationPrincipal AuthUser user, @PathVariable("ticketId") Long ticketId) {
        return ApiResponse.ok(ticketTransferService.ticketTransfer(user, ticketId));
    }

    @GetMapping("/transfer")
    public ApiResponse<MyTransferTicketsResponse> getMyTransferTickets(@AuthenticationPrincipal AuthUser user) {
        return ApiResponse.ok(ticketTransferService.getMyTransferTicket(user));
    }
}
