package io.nugulticket.sns.service;

import io.nugulticket.config.AWSSNQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SnsService {
    private final AWSSNQConfig awsConfig;

    /**
     * PaymentTopic SNS에 메시지를 삽입하는 메서드
     *
     * @param messageAttributes 삽입할 Attributes
     * @return message ID가 담긴 Response
     */
    public PublishResponse publishToPaymentTopic(Map<String, MessageAttributeValue> messageAttributes) {
        PublishRequest publishRequest = getPublishRequest(awsConfig.getSnsPaymentTopicARN(), "payment", "paymenttest", messageAttributes);

        return publish(publishRequest);
    }

    private PublishResponse publish(PublishRequest publishRequest) {
        SnsClient snsClient = awsConfig.getSnsClient();
        return snsClient.publish(publishRequest);
    }

    private PublishRequest getPublishRequest(String topicArn, String groupId, String subject, Map<String, MessageAttributeValue> messageAttributes) {
        return PublishRequest.builder()
                .topicArn(topicArn)
                .subject(subject)
                .message("UUID : " + UUID.randomUUID())
                .messageAttributes(messageAttributes)
                .messageGroupId(groupId)
                .build();
    }
}
