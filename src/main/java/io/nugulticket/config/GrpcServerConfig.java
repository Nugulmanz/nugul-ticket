package io.nugulticket.config;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.nugulticket.payment.gRPC.PaymentServiceImpl;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

// 티켓 서버 자체의 gRPC 서버를 시작하는 설정 클래스
// GrpcServerConfig에서 TicketServiceImpl을 등록하고, gRPC 서버를 지정한 포트에서 실행하여 외부 요청을 받을 준비
// Spring Boot 환경에서 자동으로 서버를 시작할 수 있게 함
@Configuration
public class GrpcServerConfig {

    private Server server;

    @Bean
    public Server grpcServer (PaymentServiceImpl paymentServiceImpl) throws IOException {
        server = ServerBuilder.forPort(50051)  //티켓서버 포트 지정
                .addService(paymentServiceImpl)
                .build();

        server.start();
        System.out.println("티켓 gRPC 서버가 50051 포트에서 시작되었습니다.");

        return server;
    }

    // Spring 애플리케이션이 종료될 때 자동으로 gRPC 서버도 종료
    @PreDestroy
    public void stopGrpcServer () {
        if(server != null) {
            server.shutdown();
            System.out.println("티켓 gRPC 서버가 종료되었습니다.");
        }
    }

}
