package io.nugulticket.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
@Getter
public class AWSSNQConfig {
    @Value("${cloud.aws.sqs.credentials.access-key}")
    private String awsAccessKey;

    @Value("${cloud.aws.sqs.credentials.secret-key}")
    private String awsSecretKey;

    @Value("${cloud.aws.sqs.region.static}")
    private String awsRegion;

    @Value("${cloud.aws.sns.payment.topic.arn}")
    private String snsPaymentTopicARN;

    /**
     * snsClient 세팅
     * @return aws와 동기화 준비가 끝난 snsClient
     */
    @Bean
    public SnsClient getSnsClient() {
        return SnsClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsAccessKey, awsSecretKey)))
                .build();
    }
}
