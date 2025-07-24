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

    /**
     * SNS에 게시하는 메서드
     *
     * @param publishRequest 게시할 메시지에 필요한 정보가 담긴 Request 객체
     * @return 게시된 메시지의 id, number가 담긴 Response 객체
     */
    private PublishResponse publish(PublishRequest publishRequest) {
        SnsClient snsClient = awsConfig.getSnsClient();
        return snsClient.publish(publishRequest);
    }

    /**
     * topic, groupId, subject, messageAttribute를 사용하고 있는 request 객체를 반환하는 메서드
     *
     * @param topicArn          구독 중인 주제
     * @param groupId           구분할 groupId
     * @param subject           추가적인 내용
     * @param messageAttributes 메시지에 담길 key, value 형태의 데이터
     * @return topic, groupId, subject, messageAttribute를 사용하고 있는 request 객체
     */
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
