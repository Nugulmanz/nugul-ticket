package io.nugulticket.sqs.service;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.nugulticket.sqs.dto.TestDto;
import org.springframework.stereotype.Service;

@Service
public class SqsService {
    /**
     * Json 형태로 메시지를 받을 경우 Dto로 자동으로 변환되는지 확인
     * 결과 : OK
     */
    @SqsListener(value = "${cloud.aws.sqs.name}")
    public void receiveMessage(TestDto testDto) {
        System.out.println("Received message: " + testDto.getId());
    }
}