package io.nugulticket.ticket.service;

import io.nugulticket.ticket.dto.response.TicketCancelledResponse;
import io.nugulticket.ticket.dto.response.TicketCompletedResponse;
import io.nugulticket.ticket.dto.response.TicketReservedResponse;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.enums.TicketStatus;
import io.nugulticket.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketInquiryService {

    private final TicketRepository ticketRepository;

    @Transactional(readOnly = true)
    public List<TicketReservedResponse> reservedTicket(Long buyerId) {

        List<Ticket> reservedTickets = ticketRepository.findAllByStatusAndBuyerId(TicketStatus.RESERVED, buyerId);

        return reservedTickets.stream()
                .map(TicketReservedResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketCancelledResponse> cancelledTicket(Long buyerId) {

        List<Ticket> cancelledTickets = ticketRepository.findAllByStatusAndBuyerId(TicketStatus.CANCELLED, buyerId);

        return cancelledTickets.stream()
                .map(TicketCancelledResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketCompletedResponse> completedTicket(Long buyerId) {

        List<Ticket> completedTickets = ticketRepository.findAllByStatusAndBuyerId(TicketStatus.COMPLETE, buyerId);

        return completedTickets.stream()
                .map(TicketCompletedResponse::new)
                .toList();
    }
}
