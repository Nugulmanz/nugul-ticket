package io.nugulticket.dashboard.service;

import io.nugulticket.common.AuthUser;
import io.nugulticket.dashboard.dto.acceptRefund.AcceptRefundRequest;
import io.nugulticket.dashboard.dto.acceptRefund.AcceptRefundResponse;
import io.nugulticket.dashboard.dto.authorityAccept.AuthorityAcceptResponse;
import io.nugulticket.dashboard.dto.getMyEvents.getMyEventsResponse;
import io.nugulticket.event.entity.Event;
import io.nugulticket.event.service.EventService;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.service.TicketService;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.enums.UserRole;
import io.nugulticket.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final UserService userService;
    private final EventService eventService;
    private final TicketService ticketService;

    @Transactional
    public AuthorityAcceptResponse authorityAccept(AuthUser authUser,Long userId) {
        if(!authUser.getAuthorities().equals(UserRole.Authority.ADMIN)){
            log.info("어드민 유저만 접근할 수 있습니다.");
        }
        // 신청한 유저가 아닌 경우 확인 필요
        User user = userService.getUser(userId);
        user.becomeSeller();
        return new AuthorityAcceptResponse(user.getId(),user.getUserRole());
    }


    public List<getMyEventsResponse> getMyEvent(AuthUser authUser) {
        List<Event> eventList = eventService.getEventFromUserId(authUser.getId());
        List<getMyEventsResponse> resDto = eventList.stream().map(getMyEventsResponse::new).toList();
        return resDto;

    }

    public AcceptRefundResponse acceptRefund(AuthUser authUser, long eventId, long ticketId, AcceptRefundRequest reqDto){
//        System.out.println(authUser.getAuthorities());
//
//        if(!authUser.getAuthorities().equals(UserRole.Authority.SELLER)){
//            throw new RuntimeException(); // 판매자가 아닌 사람인 경우
//        }
        Ticket ticket = ticketService.getRefundTicket(reqDto.getUserId(),eventId,ticketId);

        ticket.cancel();

        return new AcceptRefundResponse(ticket);

    }
}
