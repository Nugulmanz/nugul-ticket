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

    public void setOtpVerified(Long userId) {
        String key = getRedisKey(userId);
        redisTemplate.opsForValue().set(key, "true", OTP_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    public boolean isOtpVerified(Long userId) {
        String key = getRedisKey(userId);
        String value = redisTemplate.opsForValue().get(key);
        return "true".equals(value);
    }

    public void incrementFailedOtpAttempts(Long userId) {
        String key = getFailedAttemptsKey(userId);
        redisTemplate.opsForValue().increment(key);
    }

    public int getFailedOtpAttempts(Long userId) {
        String key = getFailedAttemptsKey(userId);
        String attempts = redisTemplate.opsForValue().get(key);
        return attempts != null ? Integer.parseInt(attempts) : 0;
    }

    public void resetFailedOtpAttempts(Long userId) {
        String key = getFailedAttemptsKey(userId);
        redisTemplate.delete(key);
    }

    public void lockAccount(Long userId) {
        String key = getLockStatusKey(userId);
        redisTemplate.opsForValue().set(key, "locked"); // 무기한 잠금
    }

    public boolean isAccountLocked(Long userId) {
        String key = getLockStatusKey(userId);
        return "locked".equals(redisTemplate.opsForValue().get(key));
    }

    public void unlockAccount(Long userId) {
        String key = getLockStatusKey(userId);
        redisTemplate.delete(key);
    }

    public void setCodeForUnlock(Long userId, String code) {
        String key = getUnlockCodeKey(userId);
        redisTemplate.opsForValue().set(key, code, UNLOCK_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES); // 예: 15분 동안 유효
    }

    public boolean verifyUnlockCode(Long userId, String code) {
        String key = getUnlockCodeKey(userId);
        String storedCode = redisTemplate.opsForValue().get(key);
        return code.equals(storedCode);
    }

    private String getRedisKey(Long userId) {
        return "otp:verified:" + userId;
    }

    private String getFailedAttemptsKey(Long userId) {
        return "otp:failed_attempts:" + userId;
    }

    private String getLockStatusKey(Long userId) {
        return "otp:lock_status:" + userId;
    }

    private String getUnlockCodeKey(Long userId) {
        return "otp:unlock_code:" + userId;
    }
}
