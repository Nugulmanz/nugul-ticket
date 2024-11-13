package io.nugulticket.payment;

import io.nugulticket.auction.service.AuctionService;
import io.nugulticket.ticket.entity.Ticket;
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

    /**
     * 결제 승인이 이뤄졋을 때, 해당 티켓에 대한 후처리를 진행할 메서드
     * @param type 구매 타입 ( 충전, 티켓 예매, 양도 )
     * @param ticketId 후처리를 진행할 Ticket Id
     * @param userId 결제를 진행한 userId
     */
    public void successfully(String type, Long ticketId, Long userId) {

        switch(type) {
            case "ticket":
                ticketService.reserveTicket(ticketId);
                break;
            case "transfer":
                ticketTransferService.applyTransferAfterPayment(ticketId, userId);
                break;
            case "charge":
                break;

        };
    }

    /**
     * 결제 거부 / 실패가 발생했을 때, 해당 티켓에 대한 후처리를 진행할 메서드
     * @param type 구매 타입 ( 충전, 티켓 예매, 양도 )
     * @param ticketId 후처리를 진행할 Ticket Id
     */
    public void fail(String type, Long ticketId) {

        switch(type) {
            case "ticket":
                ticketService.cancelTicket(ticketId);
                Ticket ticket = ticketService.getTicket(ticketId);
                ticket.rollbackSeat();
                break;
            case "transfer":
                ticketTransferService.cancelTransferAfterPayment(ticketId);
                break;
            case "charge":
                break;

        };
    }

}
