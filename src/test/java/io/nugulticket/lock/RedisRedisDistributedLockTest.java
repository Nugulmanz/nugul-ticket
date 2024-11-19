package io.nugulticket.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisRedisDistributedLockTest {

    @InjectMocks
    private RedisDistributedLockAspect redisDistributedLockAspect; // AOP 테스트 대상

    @Mock
    private RedissonClient redissonClient; // Redis 클라이언트 모킹

    @Mock
    private RLock mockLock; // 락 객체 모킹

    @Test
    void testFairLockAcquisition() throws Throwable {
        // Given
        String lockKey = "testLockKey";
        when(redissonClient.getFairLock(lockKey)).thenReturn(mockLock);
        when(mockLock.tryLock(10, 5, TimeUnit.SECONDS)).thenReturn(true); // 락 획득 성공

        // Mock ProceedingJoinPoint
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.proceed()).thenReturn("Success!");

        // Mock FairLock annotation
        RedisDistributedLock redisDistributedLock = mock(RedisDistributedLock.class);
        when(redisDistributedLock.key()).thenReturn(lockKey);

        // When
        Object result = redisDistributedLockAspect.applyFairLock(joinPoint, redisDistributedLock);

        // Then
        assertEquals("Success!", result); // 메서드 실행 결과 확인
        verify(mockLock).tryLock(10, 5, TimeUnit.SECONDS); // 락 획득 호출 확인
        verify(mockLock).unlock(); // 락 해제 호출 확인
    }

    @Test
    void testFairLockAcquisitionFailure() throws InterruptedException {
        // Given
        String lockKey = "testLockKey";
        when(redissonClient.getFairLock(lockKey)).thenReturn(mockLock);
        when(mockLock.tryLock(10, 5, TimeUnit.SECONDS)).thenReturn(false); // 락 획득 실패

        // Mock ProceedingJoinPoint
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        RedisDistributedLock redisDistributedLock = mock(RedisDistributedLock.class);
        when(redisDistributedLock.key()).thenReturn(lockKey);

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            redisDistributedLockAspect.applyFairLock(joinPoint, redisDistributedLock);
        });

        // Then
        assertEquals("Failed to acquire lock: " + lockKey, exception.getMessage());
        verify(mockLock, never()).unlock(); // 락 해제 호출되지 않아야 함
    }

    @Test
    void testFairLockInterrupt() throws InterruptedException {
        // Given
        String lockKey = "testLockKey";
        when(redissonClient.getFairLock(lockKey)).thenReturn(mockLock);
        when(mockLock.tryLock(10, 5, TimeUnit.SECONDS)).thenThrow(new InterruptedException()); // 락 중단

        // Mock ProceedingJoinPoint
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        RedisDistributedLock redisDistributedLock = mock(RedisDistributedLock.class);
        when(redisDistributedLock.key()).thenReturn(lockKey);

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            redisDistributedLockAspect.applyFairLock(joinPoint, redisDistributedLock);
        });

        // Then
        assertEquals("Fair Lock execution interrupted", exception.getMessage());
        verify(mockLock, never()).unlock(); // 락 해제 호출되지 않아야 함
    }
}
