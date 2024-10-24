package io.nugulticket.ticket.controller;

import io.nugulticket.ticket.dto.createTicket.CreateTicketRequest;
import io.nugulticket.ticket.dto.createTicket.CreateTicketResponse;
import io.nugulticket.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/tickets/v1")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public CreateTicketResponse createTicket(@RequestBody CreateTicketRequest reqDto,
                                             @RequestParam Long userId) {
        return ticketService.createTicket(reqDto, userId);
    }
}
