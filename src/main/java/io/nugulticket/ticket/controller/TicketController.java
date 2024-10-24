package io.nugulticket.ticket.controller;

import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.ApiResponse;
import io.nugulticket.ticket.dto.createTicket.CreateTicketRequest;
import io.nugulticket.ticket.dto.createTicket.CreateTicketResponse;
import io.nugulticket.ticket.dto.refundTicket.RefundTicketResponse;
import io.nugulticket.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets/v1")
public class TicketController {

    private final TicketService ticketService;

    @PatchMapping("/{ticketId}/refund")
    public ResponseEntity<RefundTicketResponse> refundTicket(@PathVariable Long ticketId,
                             @AuthenticationPrincipal AuthUser authUser) {
        RefundTicketResponse resDto = ticketService.refundTicket(ticketId, authUser);
        return ResponseEntity.ok().body(resDto);
    }

    @PostMapping
    public ApiResponse<CreateTicketResponse> createTicket(@RequestBody CreateTicketRequest reqDto,
                                                          @RequestParam Long userId) {
        return ApiResponse.ok(ticketService.createTicket(reqDto, userId));
    }
}
