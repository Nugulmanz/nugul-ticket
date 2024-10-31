package io.nugulticket.payment.dto.request;

import lombok.Getter;

@Getter
public class PaymentRequest {
    String paymentKey;
    String orderId;
    int amount;
    long userId;
    String orderName;
    String email;
    String orderType;
    String ticketId;
};