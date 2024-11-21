package io.nugulticket.lock;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class RedisDistributedLockAspect {

    private final RedissonClient redissonClient;

    public RedisDistributedLockAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(redisDistributedLock)")
    public Object applyFairLock(ProceedingJoinPoint joinPoint, RedisDistributedLock redisDistributedLock) throws Throwable {
        log.info("applyFairLock");

        // SpEL 파싱을 위한 설정
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String keyExpression = redisDistributedLock.key();
        EvaluationContext context = new StandardEvaluationContext();

        // 메서드 파라미터 이름 및 값을 컨텍스트에 추가
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = signature.getParameterNames(); // 파라미터 이름 가져오기
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        // SpEL 표현식 평가
        String lockKey = parser.parseExpression(keyExpression).getValue(context, String.class);
        log.info("Lock Key: {}", lockKey);

        RLock lock = redissonClient.getFairLock(lockKey); // 공정락 생성

        try {
            // 락 대기 시간 @초, 락 유지 시간 @초 설정
            if (lock.tryLock(5, 3, TimeUnit.SECONDS)) {
                try {
                    return joinPoint.proceed(); // 메서드 실행
                } finally {
                    log.info("fair lock unlock");
                    if (lock.isHeldByCurrentThread()) { // 현재 스레드가 락을 보유한 경우에만 해제
                    lock.unlock();
                    }
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