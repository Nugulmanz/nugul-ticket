package io.nugulticket.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentInfoResponse {
    private String orderId;
    private long userId;
    private int amount;
    private boolean success;
    private String message;
}
