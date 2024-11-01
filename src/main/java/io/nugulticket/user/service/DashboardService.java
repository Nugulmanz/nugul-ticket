package io.nugulticket.user.service;

import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.event.entity.Event;
import io.nugulticket.event.service.EventService;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.service.TicketService;
import io.nugulticket.user.dto.acceptRefund.AcceptRefundRequest;
import io.nugulticket.user.dto.acceptRefund.AcceptRefundResponse;
import io.nugulticket.user.dto.authorityAccept.AuthorityAcceptResponse;
import io.nugulticket.user.dto.getMyEvents.getMyEventsResponse;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final UserService userService;
    private final EventService eventService;
    private final TicketService ticketService;

    /**
     * 판매자 승인
     *
     * @param authUser : 로그인 유저
     * @param userId : 판매자로 만들어줄 유저
     * @return : 판매자로 바뀐 user, userRole
     */
    @Transactional
    public AuthorityAcceptResponse authorityAccept(AuthUser authUser,Long userId) {
        if(!authUser.getAuthorities().equals(UserRole.Authority.ADMIN)){
            throw new ApiException(ErrorStatus._FORBIDDEN_USER);
        }

        User user = userService.getUser(userId);
        user.becomeSeller();
        return new AuthorityAcceptResponse(user.getId(),user.getUserRole());
    }


    /**
     * 판매자가 소유한 공연 리스트 조회
     *
     * @param authUser : 로그인 유저
     * @return : 로그인 유저가 소유한 공연 리스트
     */
    public List<getMyEventsResponse> getMyEvent(AuthUser authUser) {
        List<Event> eventList = eventService.getEventFromUserId(authUser.getId());
        List<getMyEventsResponse> resDto = eventList.stream().map(getMyEventsResponse::new).toList();
        return resDto;

    }

    /**
     *
     * @param authUser : 로그인 유저
     * @param eventId : 이벤트 아이디
     * @param ticketId : 티켓 아이디
     * @param reqDto : 환불한 유저
     * @return : 티켓 아이디와 상태
     */
    public AcceptRefundResponse acceptRefund(AuthUser authUser, long eventId, long ticketId, AcceptRefundRequest reqDto){
        if(!authUser.getAuthorities().equals(UserRole.Authority.SELLER)){
            throw new ApiException(ErrorStatus._FORBIDDEN_ROLE);
        }
        Ticket ticket = ticketService.getRefundTicket(reqDto.getUserId(),eventId,ticketId);
        ticket.cancel();

        return new AcceptRefundResponse(ticket);

    }
}
