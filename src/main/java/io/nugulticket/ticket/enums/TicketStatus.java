package io.nugulticket.ticket.enums;

import io.nugulticket.user.enums.UserRole;

import java.util.Arrays;

public enum TicketStatus {
    RESERVED, CANCELLED, WAITTRANSFER, TRANSFERRED, AUCTION,COMPLETE;


    public static TicketStatus of(String status) {
        return Arrays.stream(TicketStatus.values())
                .filter(r -> r.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상태입니다"));
    }
}