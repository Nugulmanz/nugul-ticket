package io.nugulticket.common.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    // ALB health check
    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

    // ec2 에서 테스트
    @GetMapping("/health/test")
    public String connectCheck() {
        return "Spring Boot application is accessible!";
    }
}
