package io.nugulticket.otp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpRedisService {


    private final StringRedisTemplate redisTemplate;
    private static final long OTP_EXPIRE_MINUTES = 60;
    private static final long UNLOCK_CODE_EXPIRE_MINUTES = 15;

    /**
     * 사용자 OTP 인증 상태를 Redis에 저장합니다.
     *
     * @param userId 사용자 ID
     */
    public void setOtpVerified(Long userId) {
        String key = getRedisKey(userId);
        redisTemplate.opsForValue().set(key, "true", OTP_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 사용자 OTP 인증 여부를 확인합니다.
     *
     * @param userId 사용자 ID
     * @return OTP 인증 여부 (true: 인증 완료, false: 인증 미완료)
     */
    public boolean isOtpVerified(Long userId) {
        String key = getRedisKey(userId);
        String value = redisTemplate.opsForValue().get(key);
        return "true".equals(value);
    }

    /**
     * 사용자 OTP 실패 횟수를 증가시킵니다.
     *
     * @param userId 사용자 ID
     */
    public void incrementFailedOtpAttempts(Long userId) {
        String key = getFailedAttemptsKey(userId);
        redisTemplate.opsForValue().increment(key);
    }

    /**
     * 사용자 OTP 실패 횟수를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return OTP 실패 횟수
     */
    public int getFailedOtpAttempts(Long userId) {
        String key = getFailedAttemptsKey(userId);
        String attempts = redisTemplate.opsForValue().get(key);
        return attempts != null ? Integer.parseInt(attempts) : 0;
    }

    /**
     * 사용자 OTP 실패 횟수를 초기화합니다.
     *
     * @param userId 사용자 ID
     */
    public void resetFailedOtpAttempts(Long userId) {
        String key = getFailedAttemptsKey(userId);
        redisTemplate.delete(key);
    }

    /**
     * 사용자의 계정을 잠금 상태로 설정합니다.
     *
     * @param userId 사용자 ID
     */
    public void lockAccount(Long userId) {
        String key = getLockStatusKey(userId);
        redisTemplate.opsForValue().set(key, "locked"); // 무기한 잠금
    }

    /**
     * 사용자의 계정이 잠겨 있는지 확인합니다.
     *
     * @param userId 사용자 ID
     * @return 계정 잠금 상태 (true: 잠금, false: 잠금 해제)
     */
    public boolean isAccountLocked(Long userId) {
        String key = getLockStatusKey(userId);
        return "locked".equals(redisTemplate.opsForValue().get(key));
    }

    /**
     * 사용자의 계정 잠금을 해제합니다.
     *
     * @param userId 사용자 ID
     */
    public void unlockAccount(Long userId) {
        String key = getLockStatusKey(userId);
        redisTemplate.delete(key);
    }

    /**
     * 사용자 계정 잠금 해제 코드를 Redis에 저장합니다.
     *
     * @param userId 사용자 ID
     * @param code   잠금 해제 코드
     */
    public void setCodeForUnlock(Long userId, String code) {
        String key = getUnlockCodeKey(userId);
        redisTemplate.opsForValue().set(key, code, UNLOCK_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES); // 예: 15분 동안 유효
    }

    /**
     * 사용자 계정 잠금 해제 코드가 올바른지 검증합니다.
     *
     * @param userId 사용자 ID
     * @param code   사용자 입력 잠금 해제 코드
     * @return 잠금 해제 코드 검증 결과 (true: 일치, false: 불일치)
     */
    public boolean verifyUnlockCode(Long userId, String code) {
        String key = getUnlockCodeKey(userId);
        String storedCode = redisTemplate.opsForValue().get(key);
        return code.equals(storedCode);
    }

    /**
     * OTP 인증 상태에 대한 Redis 키를 생성합니다.
     *
     * @param userId 사용자 ID
     * @return Redis 키 (형식: otp:verified:{userId})
     */
    private String getRedisKey(Long userId) {
        return "otp:verified:" + userId;
    }

    /**
     * OTP 실패 횟수에 대한 Redis 키를 생성합니다.
     *
     * @param userId 사용자 ID
     * @return Redis 키 (형식: otp:failed_attempts:{userId})
     */
    private String getFailedAttemptsKey(Long userId) {
        return "otp:failed_attempts:" + userId;
    }

    /**
     * 계정 잠금 상태에 대한 Redis 키를 생성합니다.
     *
     * @param userId 사용자 ID
     * @return Redis 키 (형식: otp:lock_status:{userId})
     */
    private String getLockStatusKey(Long userId) {
        return "otp:lock_status:" + userId;
    }

    /**
     * 잠금 해제 코드에 대한 Redis 키를 생성합니다.
     *
     * @param userId 사용자 ID
     * @return Redis 키 (형식: otp:unlock_code:{userId})
     */
    private String getUnlockCodeKey(Long userId) {
        return "otp:unlock_code:" + userId;
    }
}
