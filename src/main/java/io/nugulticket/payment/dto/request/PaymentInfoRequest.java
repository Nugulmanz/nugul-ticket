package io.nugulticket.payment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentInfoRequest {
    String orderId;
    long userId;

}
