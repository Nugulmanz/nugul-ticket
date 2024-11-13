package io.nugulticket.sqs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.nugulticket.config.SQSProtocol;
import io.nugulticket.config.SQSUtility;
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

    /**
     * Json 형태로 메시지를 받을 경우 Dto로 자동으로 변환되는지 확인
     * 결과 : OK
     */
    @SqsListener(value = "${cloud.aws.sqs.name}")
    public void receiveMessage(Message message) throws JsonProcessingException {
        System.out.println(message);
        Map<String , MessageAttributeValue> messageAttribute = sqsUtility.parseMessage(message);
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

    private void successPayment(Map<String , MessageAttributeValue> messageAttribute) {
        SQSSuccessPayment successPaymentDto = new SQSSuccessPayment();
        successPaymentDto.fromSQSAttributes(messageAttribute);

        ticketService.reserveTicket(successPaymentDto.getTicketId());
    }

    private void failPayment(Map<String , MessageAttributeValue> messageAttribute) {
        SQSFailPayment failPaymentDto = new SQSFailPayment();
        failPaymentDto.fromSQSAttributes(messageAttribute);

        ticketService.cancelTicket(failPaymentDto.getTicketId());

        System.out.println(failPaymentDto.getMessage());
    }
}