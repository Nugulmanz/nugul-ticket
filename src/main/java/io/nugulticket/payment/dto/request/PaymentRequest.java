package io.nugulticket.payment.dto.request;

import lombok.Data;

@Data
public class PaymentRequest {
    String paymentKey;
    String orderId;
    int amount;
};