package io.nugulticket.lock;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class RedisDistributedLockAspect {

    private final RedissonClient redissonClient;

    public RedisDistributedLockAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(redisDistributedLock)")
    public Object applyFairLock(ProceedingJoinPoint joinPoint, RedisDistributedLock redisDistributedLock) throws Throwable {
        log.info("applyFairLock");
        String lockKey = redisDistributedLock.key(); // 애노테이션에 지정한 키
        RLock lock = redissonClient.getFairLock(lockKey); // 공정락 생성

        try {
            // 락 대기 시간 10초, 락 유지 시간 5초 설정
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                try {
                    return joinPoint.proceed(); // 메서드 실행
                } finally {
                    log.info("fair lock finally");
//                    if (lock.isHeldByCurrentThread()) { // 현재 스레드가 락을 보유한 경우에만 해제
                    lock.unlock();
//                    }
                }
            } else {
                log.info("fair lock fail");
                throw new RuntimeException("Failed to acquire lock: " + lockKey);
            }
        } catch (InterruptedException e) {
            log.info("fair lock interrupted");
            Thread.currentThread().interrupt();
            throw new RuntimeException("Fair Lock execution interrupted", e);
        }

    }
}