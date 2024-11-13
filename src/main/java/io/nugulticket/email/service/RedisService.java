package io.nugulticket.email.service;

import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 인증 Code의 유효기간을 지정하고 Redis에 저장하는 메서드
     *     email을 key값 code를 value로 하여 3분동안 저장한다.
     * @param email 인증 메일을 수신할 유저 이메일 정보 ( Key Value )
     * @param code 6자리 숫자로 이루어진 인증 번호
     */
    public void setCode(String email,String code){
        ValueOperations<String, Object> valOperations = redisTemplate.opsForValue();
        //만료기간 3분
        valOperations.set(email,code,180, TimeUnit.SECONDS);
    }

    /**
     * 인증 Code가 유효한지 확인하고 반환하는 메서드
     *     key값인 email에 있는 value를 가져온다.
     * @param email 인증 메일을 수신할 유저 이메일 정보 ( Key Value )
     * @return 인증 번호 - 인증 Code가 유효한 경우
     */
    public String getCode(String email){
        ValueOperations<String, Object> valOperations = redisTemplate.opsForValue();
        Object code = valOperations.get(email);
        if(code == null){
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }
        return code.toString();
    }
}
