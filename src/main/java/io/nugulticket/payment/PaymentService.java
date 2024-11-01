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

    public PaymentResponse successfully(String type, Long ticketId, Long userId) {

        switch(type) {
            case "ticket":
                ticketService.reserveTicket(ticketId);
                break;
            case "transfer":
                ticketTransferService.applyTransferAfterPayment(ticketId, userId);
                break;
            case "charge":
//
                break;

        };

        return null;
    }

    public PaymentResponse fail(String type, Long ticketId, Long userId) {

        switch(type) {
            case "ticket":
                ticketService.cancelTicket(ticketId);
                break;
            case "transfer":
                ticketTransferService.cancelTransferAfterPayment(ticketId);
                break;
            case "charge":
//
                break;

        };

        return null;
    }

}
