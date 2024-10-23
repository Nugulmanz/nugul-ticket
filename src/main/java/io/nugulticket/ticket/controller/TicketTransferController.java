package io.nugulticket.ticket.controller;

import io.nugulticket.ticket.dto.response.GetMyTransferTicketsResponse;
import io.nugulticket.ticket.dto.response.TicketTransferApplyResponse;
import io.nugulticket.ticket.dto.response.TicketTransferCancleResponse;
import io.nugulticket.ticket.dto.response.TicketTransferResponse;
import io.nugulticket.ticket.service.TicketTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 양도 받은 티켓, 티켓 양도하기 / 신청과 같이 티켓 양도 기능 관련 컨트롤러
 * AuthUser 클래스가 추가될 경우 내용 추가 예정
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/tickets/v1")
public class TicketTransferController {

    private final TicketTransferService ticketTransferService;

    @PostMapping(value = "/{ticketId}/transfer/apply")
    public TicketTransferApplyResponse applyTransfer(@PathVariable("ticketId") Long ticketId) {
        return ticketTransferService.applyTransfer(ticketId);
    }

    @PatchMapping(value = "/{ticketId}")
    public TicketTransferCancleResponse cancelTransfer(@PathVariable("ticketId") Long ticketId) {
        return ticketTransferService.cancelTransfer(ticketId);
    }

    @PostMapping(value = "/{ticketId}/transfer")
    public TicketTransferResponse createTransfer(@PathVariable("ticketId") Long ticketId) {
        return ticketTransferService.ticketTransfer(ticketId);
    }

    @GetMapping(value = "/transfer")
    public GetMyTransferTicketsResponse getMyTransferTickets() {
        return ticketTransferService.getMyTransferTicket();
    }
    // AuthUser 추가시 옮길 내용
//    @PostMapping(value = "{ticketId}/transfer/apply")
//    public String applyTransfer(@AuthenticationPrincipal AuthUser, @PathVariable("ticketId") Long ticketId) {
//        ticketTransferService.applyTransfer(ticketId)
//    }
}
