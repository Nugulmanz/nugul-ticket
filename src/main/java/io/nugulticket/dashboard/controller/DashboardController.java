package io.nugulticket.dashboard.controller;

import io.nugulticket.common.AuthUser;
import io.nugulticket.dashboard.dto.authorityAccept.AuthorityAcceptRequest;
import io.nugulticket.dashboard.dto.authorityAccept.AuthorityAcceptResponse;
import io.nugulticket.dashboard.dto.getMyEvents.getMyEventsResponse;
import io.nugulticket.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    @PatchMapping("/admin/v1/permissions-requests/accept")
    public ResponseEntity<AuthorityAcceptResponse> authorityAccept(@AuthenticationPrincipal AuthUser authUser,
                                                   @RequestBody AuthorityAcceptRequest reqDto){
        AuthorityAcceptResponse resDto = dashboardService.authorityAccept(authUser, reqDto.getUserId());
        return ResponseEntity.ok().body(resDto);
    }

    @GetMapping("/seller/v1/event/")
    public ResponseEntity<List<getMyEventsResponse>> getMyEvents(@AuthenticationPrincipal AuthUser authUser){
        List<getMyEventsResponse> resDto =  dashboardService.getMyEvent(authUser);
        return ResponseEntity.ok().body(resDto);
    }

    @PatchMapping("/seller/v1/event/{eventId}/ticket/{ticketId}/refund")
    public void acceptRefund(@AuthenticationPrincipal AuthUser authUser,
                             @PathVariable long eventId,
                             @PathVariable long ticketId){
        dashboardService.acceptRefund(authUser, eventId, ticketId);
    }


}
