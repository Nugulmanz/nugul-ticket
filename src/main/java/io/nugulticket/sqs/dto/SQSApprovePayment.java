package io.nugulticket.sqs.dto;

import io.nugulticket.config.SQSProtocol;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SQSApprovePayment implements SQSDto {
    private String type;
    private long ticketId;
    private String paymentKey;
    private String orderId;
    private long userId;

    @Override
    public void fromSQSAttributes(Map<String, MessageAttributeValue> attributes) {
        type = SQSProtocol.TYPE_APPROVE_PAYMENT;
        ticketId = Long.parseLong(attributes.get(SQSProtocol.ATTRIBUTE_NAME_TICKET_ID).stringValue());
        paymentKey = attributes.get(SQSProtocol.ATTRIBUTE_NAME_PAYMENT_KEY).stringValue();
        orderId = attributes.get(SQSProtocol.ATTRIBUTE_NAME_ORDER_ID).stringValue();
        userId = Long.parseLong(attributes.get(SQSProtocol.ATTRIBUTE_NAME_USER_ID).stringValue());
    }

    @Override
    public Map<String, software.amazon.awssdk.services.sns.model.MessageAttributeValue> toSNSAttributes() {
        Map<String, software.amazon.awssdk.services.sns.model.MessageAttributeValue> attributes = new HashMap<>();

        attributes.put(SQSProtocol.ATTRIBUTE_NAME_TICKET_ID, software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                .dataType("Number")
                .stringValue(String.valueOf(ticketId))
                .build());

        attributes.put(SQSProtocol.ATTRIBUTE_NAME_TYPE, software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                .dataType("String")
                .stringValue(SQSProtocol.TYPE_PRE_ORDER)
                .build());

        attributes.put(SQSProtocol.ATTRIBUTE_NAME_PAYMENT_KEY, software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                .dataType("String")
                .stringValue(paymentKey)
                .build());

        attributes.put(SQSProtocol.ATTRIBUTE_NAME_ORDER_ID, software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                .dataType("String")
                .stringValue(orderId)
                .build());

        attributes.put(SQSProtocol.ATTRIBUTE_NAME_USER_ID, software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                .dataType("Number")
                .stringValue(String.valueOf(userId))
                .build());

        return attributes;
    }
}
