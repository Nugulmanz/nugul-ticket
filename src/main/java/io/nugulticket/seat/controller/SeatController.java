package io.nugulticket.seat.controller;

import io.nugulticket.common.apipayload.ApiResponse;
import io.nugulticket.seat.dto.SeatResponse;
import io.nugulticket.seat.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/seats/v1")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @GetMapping("/{eventTimeId}")
    public ApiResponse<List<SeatResponse>> getSeats(@PathVariable int eventTimeId) {
        return ApiResponse.ok(seatService.getSeats(eventTimeId));
    }
}
