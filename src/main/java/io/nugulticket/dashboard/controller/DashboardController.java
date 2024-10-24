package io.nugulticket.dashboard.controller;

import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.ApiResponse;
import io.nugulticket.dashboard.dto.acceptRefund.AcceptRefundRequest;
import io.nugulticket.dashboard.dto.acceptRefund.AcceptRefundResponse;
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
    public ApiResponse<AuthorityAcceptResponse> authorityAccept(@AuthenticationPrincipal AuthUser authUser,
                                                                @RequestBody AuthorityAcceptRequest reqDto) {
        AuthorityAcceptResponse resDto = dashboardService.authorityAccept(authUser, reqDto.getUserId());
        return ApiResponse.ok(resDto);
    }

    @GetMapping("/seller/v1/event/")
    public ApiResponse<List<getMyEventsResponse>> getMyEvents(@AuthenticationPrincipal AuthUser authUser) {
        List<getMyEventsResponse> resDto = dashboardService.getMyEvent(authUser);
        return ApiResponse.ok(resDto);
    }

    @PatchMapping("/seller/v1/event/{eventId}/ticket/{ticketId}/refund")
    public ApiResponse<AcceptRefundResponse> acceptRefund(@AuthenticationPrincipal AuthUser authUser,
                                                             @PathVariable long eventId,
                                                             @PathVariable long ticketId,
                                                             @RequestBody AcceptRefundRequest reqDto) {
        AcceptRefundResponse resDto = dashboardService.acceptRefund(authUser, eventId, ticketId, reqDto);
        return ApiResponse.ok(resDto);
    }


}
