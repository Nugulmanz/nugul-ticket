package io.nugulticket.sqs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@RequiredArgsConstructor
public class RealtimeMessageSender {

    @Value("${cloud.aws.sqs.url-realtime}")
    private String awsSqsRealtimeUrl;
    private final SqsClient sqsClient;

    /**
     * 지정된 좌석 ID와 이벤트 시간 ID를 기반으로 실시간 큐(SQS)에 메시지를 전송
     * 메시지는 JSON 형식으로 작성되며, 좌석의 상태 정보를 포함
     * @param seatId 전송할 좌석의 ID
     * @param eventTimeId 전송할 특정 공연 시간의 ID
     */
    public void sendMessage(Long seatId, int eventTimeId) {
        String messageBody = String.format("{\"seatId\":%d, \"status\":\"RESERVED\", \"eventTimeId\":%d}", seatId, eventTimeId);
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(awsSqsRealtimeUrl)
                .messageBody(messageBody)
                .build();
        sqsClient.sendMessage(request);
    }
}
