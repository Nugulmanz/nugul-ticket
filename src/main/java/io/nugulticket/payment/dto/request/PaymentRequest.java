package io.nugulticket.payment.dto.request;

import io.nugulticket.ticket.dto.response.TicketNeedPaymentResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRequest {
    String paymentKey;
    String orderId;
    int amount;
    long userId;
    String orderName;
    String email;
    String orderType;
    Long ticketId;

    public static PaymentRequest of(TicketNeedPaymentResponse response) {
        PaymentRequest request = new PaymentRequest();
        request.paymentKey = "";
        request.orderId = response.getOrderId();
        request.amount = response.getAmount();
        request.userId = response.getUserId();
        request.orderName = response.getOrderName();
        request.email = response.getEmail();
        request.orderType = response.getOrderType();
        request.ticketId = response.getTicketId();
        return request;
    }

}