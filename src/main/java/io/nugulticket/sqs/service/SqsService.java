package io.nugulticket.sqs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.nugulticket.config.SQSProtocol;
import io.nugulticket.config.SQSUtility;
import io.nugulticket.seat.entity.Seat;
import io.nugulticket.sqs.dto.SQSFailPayment;
import io.nugulticket.sqs.dto.SQSSuccessPayment;
import io.nugulticket.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class SqsService {

    private final SQSUtility sqsUtility;
    private final TicketService ticketService;
    private final RealtimeMessageSender realtimeMessageSender;

    /**
     * SQS에 message가 polling 된 경우, 해당 내용을 가져오는 Listener
     *
     * @param message SQS에서 polling 해온 Message
     */
    @SqsListener(value = "${cloud.aws.sqs.name}")
    public void receiveMessage(Message message) throws JsonProcessingException {
        System.out.println(message);
        Map<String, MessageAttributeValue> messageAttribute = sqsUtility.parseMessage(message);
        final String type = sqsUtility.getType(messageAttribute);

        switch (type) {
            case SQSProtocol.TYPE_SUCCESS_PAYMENT:
                successPayment(messageAttribute);
                break;

            case SQSProtocol.TYPE_CANCEL_PAYMENT:
                failPayment(messageAttribute);
                break;
        }
    }

    /**
     * 결제 승인 성공 시 실행될 메서드 ( 티켓 상태를 예매 완료로 변경 )
     *
     * @param messageAttribute 결제 승인 성공 시, SQS Message에 담겨온 Attribute
     */
    private void successPayment(Map<String, MessageAttributeValue> messageAttribute) {
        SQSSuccessPayment successPaymentDto = new SQSSuccessPayment();
        successPaymentDto.fromSQSAttributes(messageAttribute);

        ticketService.reserveTicket(successPaymentDto.getTicketId());

        // 실시간 서버에 티켓 상태 변경 메세지 전송(SQS)
        Seat seat = ticketService.getTicketJoinFetchSeatAndEventTime(successPaymentDto.getTicketId()).getSeat();
        realtimeMessageSender.sendMessage(seat.getId(), seat.getEventTime().getId());
    }

    /**
     * 결제 승인 실패 시 실행될 메서드 ( 티켓 상태를 예매 취소로 변경 )
     *
     * @param messageAttribute 결제 승인 실패 시, SQS Message에 담겨온 Attribute
     */
    private void failPayment(Map<String, MessageAttributeValue> messageAttribute) {
        SQSFailPayment failPaymentDto = new SQSFailPayment();
        failPaymentDto.fromSQSAttributes(messageAttribute);

        ticketService.cancelTicket(failPaymentDto.getTicketId());

        System.out.println(failPaymentDto.getMessage());
    }
}