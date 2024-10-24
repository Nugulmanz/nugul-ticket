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

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/tickets/v1")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ApiResponse<CreateTicketResponse> createTicket(@RequestBody CreateTicketRequest reqDto,
                                                          @RequestParam Long userId) { // 프린시펄 @AuthenticationPrincipal AuthUser authUser
        CreateTicketResponse resDto =ticketService.createTicket(reqDto, userId);
        return ApiResponse.ok(resDto);
    }

    @PatchMapping("/{ticketId}/refund")
    public ApiResponse<RefundTicketResponse> refundTicket(@PathVariable Long ticketId,
                             @AuthenticationPrincipal AuthUser authUser){
        RefundTicketResponse resDto = ticketService.refundTicket(ticketId, authUser);
        return ApiResponse.ok(resDto);
    }
}
