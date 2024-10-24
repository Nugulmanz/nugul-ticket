package io.nugulticket.ticket.controller;

import io.nugulticket.ticket.dto.response.TicketCancelledResponse;
import io.nugulticket.ticket.dto.response.TicketCompletedResponse;
import io.nugulticket.ticket.dto.response.TicketReservedResponse;
import io.nugulticket.ticket.service.TicketInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketInquiryController {
    private final TicketInquiryService ticketInquiryService;

    @GetMapping("/reserved")
    public ResponseEntity<List<TicketReservedResponse>> reservedTicket(
            @RequestParam Long buyerId) {

        List<TicketReservedResponse> response = ticketInquiryService.reservedTicket(buyerId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/cancelled")
    public ResponseEntity<List<TicketCancelledResponse>> cancelledTicket(
            @RequestParam Long buyerId) {

        List<TicketCancelledResponse> response = ticketInquiryService.cancelledTicket(buyerId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<TicketCompletedResponse>> completedTicket(
            @RequestParam Long buyerId) {

        List<TicketCompletedResponse> response = ticketInquiryService.completedTicket(buyerId);

        return ResponseEntity.ok(response);
    }

}

