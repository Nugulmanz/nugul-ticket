package io.nugulticket.ticket.config;

import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.enums.TicketStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TicketUtil {
    /**
     * 해당 티켓의 공연 시간이 아직 지나지 않았는지에 대한 여부를 판단하는 메서드
     * @param ticket 공연 시간을 확인할 티켓
     * @return True : 아직 유효한 티켓 / False : 공연 시간이 지나 유효하지 않은 티켓
     */
    public boolean isValidTicket(Ticket ticket) {
        LocalDateTime now = LocalDateTime.now();

        // Seat, EventTime 나올 경우
        if (ticket.getSeat().getEventTime().getDateTime().isAfter(now)) {
            throw new IllegalArgumentException();
        }

        return now.isBefore(ticket.getPurchaseDate());
    }

    public boolean isNowStatus(Ticket ticket, TicketStatus status) {
        return ticket.getStatus() == status;
    }

    public boolean isMineTicket(Ticket ticket, Long userId) {
        return ticket.getBuyerId().equals(userId);
    }

    /**
     * 티켓이 양도 가능한 상태인지 확인하는 메서드
     *  1. 해당 티켓이 userId소유인 경우
     *  2. 티켓 상태가 Reserved인 경우
     *  3. 해당 티켓이 아직 유효한 경우
     * @param ticket 확인할 티켓
     * @param userId 소유를 확인할 유저 Id
     * @return True : 양도 가능한 상태 / False : 양도가 불가능한 상태
     */
    public boolean isAbleTicketTransfer(Ticket ticket, Long userId) {
        return isValidTicket(ticket) && isNowStatus(ticket, TicketStatus.RESERVED) && isMineTicket(ticket, userId);
    }

    /**
     * 해당 티켓에 양도 신청을 넣을 수 있는 상태인지 확인하는 메서드
     *  1. 해당 티켓이 아직 유효한 경우
     *  2. 해당 티켓이 양도 대기중인 경우
     * @param ticket 확인할 티켓
     * @return True : 양도 신청이 가능한 상태 / False : 양도 신청이 불가능한 상태
     */
    public boolean isAbleTicketApplyTransfer(Ticket ticket) {
        return isValidTicket(ticket) && isNowStatus(ticket, TicketStatus.WAITTRANSFER);
    }

    /**
     * 해당 티켓이 양도 취소가 가능한 상태인지 확인하는 메서드
     *  1. 해당 티켓이 userId소유인 경우
     *  2. 해당 티켓이 양도 대기중인 경우
     * @param ticket 확인할 티켓
     * @return True : 양도 취소가 가능한 상태 / False : 양도 취소가 불가능한 상태
     */
    public boolean isAbleTicketCancelTransfer(Ticket ticket, Long userId) {
        return isNowStatus(ticket, TicketStatus.WAITTRANSFER) && isMineTicket(ticket, userId);
    }
}
