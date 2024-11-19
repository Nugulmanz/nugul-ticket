package io.nugulticket.payment.dto.request;

import io.nugulticket.payment.entity.EventDetails;
import io.nugulticket.payment.entity.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RestRequest {
    private String orderId;
    private long userId;
    private UserDetails userDetails;
    private List<EventDetails> events;
}
