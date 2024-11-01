package io.nugulticket.payment;

import io.nugulticket.auction.service.AuctionService;
import io.nugulticket.payment.dto.response.PaymentResponse;
import io.nugulticket.ticket.service.TicketService;
import io.nugulticket.ticket.service.TicketTransferService;
import io.nugulticket.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentService {
    // Nugul Payment와 통신 ( PaymentConfirm )
    private final TicketTransferService ticketTransferService;
    private final TicketService ticketService;
    private final AuctionService auctionService; // 지갑
    private final UserService userService;

    private PaymentResponse successfully(String type, Long ticketId) {

        switch(type) {
            case "ticket":
                ticketService.reserveTicket(ticketId);
                break;
            case "transfer":
//              ticketTransferService.success(ticketId)
                break;
            case "charge":
//
                break;

        };

        return null;
    }

    private PaymentResponse fail(String type, Long ticketId) {

        switch(type) {
            case "ticket":
                ticketService.cancelTicket(ticketId);
                break;
            case "transfer":
//              ticketTransferService.failed(ticketId)
                break;
            case "charge":
//
                break;

        };

        return null;
    }

}
