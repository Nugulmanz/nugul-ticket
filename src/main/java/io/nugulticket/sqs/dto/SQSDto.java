package io.nugulticket.sqs.dto;

import java.util.Map;

public interface SQSDto {
    void fromSQSAttributes(Map<String, software.amazon.awssdk.services.sqs.model.MessageAttributeValue> attributes);

    Map<String, software.amazon.awssdk.services.sns.model.MessageAttributeValue> toSNSAttributes();
}
