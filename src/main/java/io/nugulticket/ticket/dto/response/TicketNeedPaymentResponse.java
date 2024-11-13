package io.nugulticket.ticket.dto.response;

import io.nugulticket.common.AuthUser;
import io.nugulticket.ticket.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class TicketNeedPaymentResponse {
    Long userId;
    Long ticketId;
    String orderType;
    String orderName;
    String email;
    String orderId;
    int amount;

    public static TicketNeedPaymentResponse of(Ticket ticket, AuthUser authUser, String orderType, String orderId) {
        TicketNeedPaymentResponse response = new TicketNeedPaymentResponse();

        response.userId = authUser.getId();
        response.ticketId = ticket.getTicketId();
        response.orderType = orderType;
        response.orderName = ticket.getEvent().getTitle();
        response.email = authUser.getEmail();
        response.orderId = orderId;
        response.amount = ticket.getSeat().getPrice();

        return response;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("userId", userId);
        map.put("ticketId", ticketId);
        map.put("orderType", orderType);
        map.put("orderName", orderName);
        map.put("email", email);
        map.put("orderId", orderId);
        map.put("amount", amount);

        return map;
    }
}