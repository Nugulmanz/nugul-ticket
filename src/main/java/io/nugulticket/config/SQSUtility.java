package io.nugulticket.config;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class SQSUtility {
    /**
     * Queue에서 Message를 삭제 하는 메서드
     * @param sqsAsyncClient sqs 비동기 Client
     * @param sqsUrl sqsQueue URL
     * @param message 삭제할 내용의 Message
     * @return 삭제 처리 결과가 담긴 Response 객체
     */
    public DeleteMessageResponse deleteMessage(SqsAsyncClient sqsAsyncClient, String sqsUrl, Message message) {
        // CompleteabeFuture 객체는 비동기적으로 작동하기에 Atomic을 통해 안전성을 충족시켜줘야 한다.
        AtomicReference<DeleteMessageResponse> result = new AtomicReference<>();

        CompletableFuture<DeleteMessageResponse> response = sqsAsyncClient.deleteMessage(deleteMessageRequest ->
                deleteMessageRequest.queueUrl(sqsUrl)
                        .receiptHandle(message.receiptHandle())).whenComplete(((deleteMessageResponse, throwable) -> {
            if (throwable != null) {
                System.out.println(throwable.getMessage());
            } else {
                System.out.println(deleteMessageResponse.sdkHttpResponse().isSuccessful());
                result.set(deleteMessageResponse);
            };
        }));

        return result.get();
    }

    public String getType(Map<String, software.amazon.awssdk.services.sqs.model.MessageAttributeValue> sqsAttributes) {
        return sqsAttributes.get(SQSProtocol.ATTRIBUTE_NAME_TYPE).stringValue();
    }

    public Map<String, software.amazon.awssdk.services.sns.model.MessageAttributeValue> convertSqsAttributesToSnsAttributes(Map<String, software.amazon.awssdk.services.sqs.model.MessageAttributeValue> sqsAttributes) {
        Map<String, software.amazon.awssdk.services.sns.model.MessageAttributeValue> snsAttributes = new HashMap<>();

        for (Map.Entry<String, software.amazon.awssdk.services.sqs.model.MessageAttributeValue> entry : sqsAttributes.entrySet()) {
            String key = entry.getKey();
            software.amazon.awssdk.services.sqs.model.MessageAttributeValue sqsValue = entry.getValue();

            // 변환된 SNS MessageAttributeValue를 생성
            software.amazon.awssdk.services.sns.model.MessageAttributeValue snsValue = software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                    .dataType(sqsValue.dataType())
                    .stringValue(sqsValue.stringValue())
                    .binaryValue(sqsValue.binaryValue())
                    .build();

            snsAttributes.put(key, snsValue);
        }

        return snsAttributes;
    }


}
