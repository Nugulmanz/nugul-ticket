package io.nugulticket.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisDistributedLockAspectTest {

    @InjectMocks
    private RedisDistributedLockAspect redisDistributedLockAspect;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock mockLock;

    @Test
    void testConcurrentRequests() throws InterruptedException {
        // Given
        String lockKey = "zz";
        when(redissonClient.getFairLock(lockKey)).thenReturn(mockLock);

        // 락 Mock 설정
        doAnswer(invocation -> {
            Thread.sleep(50); // 락 획득 시 약간의 지연 추가
            return true; // 락 획득 성공
        }).when(mockLock).tryLock(10, 5, TimeUnit.SECONDS);
        doNothing().when(mockLock).unlock();

        int numberOfThreads = 50; // 동시에 실행할 요청 수
        ExecutorService executorService = Executors.newFixedThreadPool(1000);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // 공유 자원
        final int[] sharedResource = {0};

        // When
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                    when(joinPoint.proceed()).thenAnswer(invocation -> {
                        synchronized (sharedResource) { // 동기화 블록
                            sharedResource[0]++;
                        }
                        return null;
                    });

                    RedisDistributedLock redisDistributedLock = mock(RedisDistributedLock.class);
                    when(redisDistributedLock.key()).thenReturn(lockKey);

                    redisDistributedLockAspect.applyFairLock(joinPoint, redisDistributedLock);
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 요청이 끝날 때까지 대기
        latch.await();
        executorService.shutdown();

        // Then
        assertEquals(numberOfThreads, sharedResource[0], "Shared resource count mismatch!");
        verify(mockLock, times(numberOfThreads)).tryLock(10, 5, TimeUnit.SECONDS);
        verify(mockLock, times(numberOfThreads)).unlock();
    }


}