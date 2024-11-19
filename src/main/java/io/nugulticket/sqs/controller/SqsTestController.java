package io.nugulticket.sqs.controller;

import io.nugulticket.sqs.service.SqsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sqs")
@RequiredArgsConstructor
public class SqsTestController {
    private final SqsService sqsService;

    // 좌석 예매 완료를 가정한 SQS 메세지 전송 테스트
    @PostMapping("/test")
    public String test(@RequestParam Long seatId, @RequestParam int eventTimeId) {
        sqsService.sendMessage(seatId, eventTimeId);
        return "Message sent = seat: "
                + seatId + ", eventTimeId: "
                + eventTimeId +", status: RESERVED";
    }
}
