package io.nugulticket.ticket.service;

import io.nugulticket.common.AuthUser;
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
    public List<TicketReservedResponse> reservedTicket(AuthUser authUser) {

        Long userId = authUser.getId();

        List<Ticket> reservedTickets = ticketRepository.findAllByStatusAndUser_Id(TicketStatus.RESERVED, userId);

        return reservedTickets.stream()
                .map(TicketReservedResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketCancelledResponse> cancelledTicket(AuthUser authUser) {

        Long userId = authUser.getId();

        List<Ticket> cancelledTickets = ticketRepository.findAllByStatusAndUser_Id(TicketStatus.CANCELLED, userId);

        return cancelledTickets.stream()
                .map(TicketCancelledResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketCompletedResponse> completedTicket(AuthUser authUser) {

        Long userId = authUser.getId();

        List<Ticket> completedTickets = ticketRepository.findAllByStatusAndUser_Id(TicketStatus.COMPLETE, userId);

        return completedTickets.stream()
                .map(TicketCompletedResponse::new)
                .toList();
    }
}
