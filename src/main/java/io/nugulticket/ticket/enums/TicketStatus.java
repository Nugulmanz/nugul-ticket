package io.nugulticket.ticket.enums;

import java.util.Arrays;

public enum TicketStatus {
    RESERVED, CANCELLED, WAITTRANSFER, TRANSFERRED, WAITING_RESERVED, AUCTION, COMPLETE, WAIT_CANCEL;


    public static TicketStatus of(String status) {
        return Arrays.stream(TicketStatus.values())
                .filter(r -> r.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상태입니다"));
    }
}