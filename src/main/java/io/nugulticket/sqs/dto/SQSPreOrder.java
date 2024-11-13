package io.nugulticket.sqs.dto;


import io.nugulticket.config.SQSProtocol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SQSPreOrder implements SQSDto {

    private String type;
    private String orderId;
    private int amount;

    @Override
    public void fromSQSAttributes(Map<String, MessageAttributeValue> attributes) {
        amount = Integer.parseInt(attributes.get(SQSProtocol.ATTRIBUTE_NAME_AMOUNT).stringValue());
        type = SQSProtocol.TYPE_PRE_ORDER;
        orderId = attributes.get(SQSProtocol.ATTRIBUTE_NAME_ORDER_ID).stringValue();
    }

    @Override
    public Map<String, software.amazon.awssdk.services.sns.model.MessageAttributeValue> toSNSAttributes() {
        Map<String, software.amazon.awssdk.services.sns.model.MessageAttributeValue> attributes = new HashMap<>();

        attributes.put(SQSProtocol.ATTRIBUTE_NAME_AMOUNT, software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                .dataType("Number")
                .stringValue(String.valueOf(amount))
                .build());

        attributes.put(SQSProtocol.ATTRIBUTE_NAME_TYPE, software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                .dataType("String")
                .stringValue(SQSProtocol.TYPE_PRE_ORDER)
                .build());

        attributes.put(SQSProtocol.ATTRIBUTE_NAME_ORDER_ID, software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                .dataType("String")
                .stringValue(orderId)
                .build());

        return attributes;
    }
}
