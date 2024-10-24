package io.nugulticket.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nugulticket.auth.dto.kakaoLogin.KakaoLoginResponse;
import io.nugulticket.auth.dto.updateKakaoUserInfo.UpdateKakaoUserInfoRequest;
import io.nugulticket.auth.dto.updateKakaoUserInfo.UpdateKakaoUserInfoResponse;
import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.common.utils.JwtUtil;
import io.nugulticket.user.dto.KakaoUserDto;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.enums.LoginType;
import io.nugulticket.user.enums.UserRole;
import io.nugulticket.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${client.id}")
    private String clientId;

    /**
     * 카카오를 통해 User계정을 로그인 하고 bearerToken을 받는 메서드
     * @param code 카카오에서 제공한 로그인 Code
     * @return 해당 유저의 bearerToken
     */
    public KakaoLoginResponse kakaoLogin(String code) throws JsonProcessingException {
        // 인가 코드로 액세스 토큰 요청
        String accessToken = getToken(code);

        // 액세스 토큰으로 카카오 사용자 정보 가져오기
        KakaoUserDto kakaoUserDto = getKakaoUserInfo(accessToken);
        kakaoUserDto.setUserRole(UserRole.USER);
        kakaoUserDto.setLoginType(LoginType.SOCIAL);

        // 해당 유저가 이미 가입되어 있는지 확인하고, 가입되어 있지 않다면 회원가입 진행
        User user = registerKakaoUserIfNeeded(kakaoUserDto);

        // jwt 토큰 반환
        String token = jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole());
        log.info("token : " + token);
        return new KakaoLoginResponse(token);
    }


    // 카카오에서 준 인가코드로 액세스토큰을 반환하는 메서드
    //kakao developers에서 요청하는대로 http형태를 만들어서 RestTemplete으로 API를 요청
    private String getToken(String code) throws JsonProcessingException {
        log.info("인가코드 : " + code);

        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId); // REST API 키 - 환경변수 설정
        body.add("redirect_uri", "http://localhost:8080/api/auth/v1/login/kakao");
        body.add("code", code);

        // 위에서 작성한 uri, headers, body로 requestEntity 생성
        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        // 카카오 쪽으로 HTTP 요청 보내고 받는건 String 타입으로 받아옴
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답 (JSON) -> 반환되는 토큰이 String 형태로 되어있어 그 토큰을 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }


    // 액세스토큰으로 사용자정보(닉네임, 이메일)을 가져오는 메서드
    private KakaoUserDto getKakaoUserInfo(String accessToken)  throws JsonProcessingException {
        log.info("accessToken : " + accessToken);
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // body엔 따로 넣어줄 게 없음
        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();

        log.info("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);
        return new KakaoUserDto(id, nickname, email);
    }


    //해당 유저가 이미 가입되어 있는지 확인하고, 가입되어 있지 않다면 회원가입을 진행하는 메서드
    private User registerKakaoUserIfNeeded(KakaoUserDto kakaoUserDto) {
        // DB 에 중복된 socialId가 있는지 확인
        Long socialId = kakaoUserDto.getId();
        User kakaoUser = userRepository.findBySocialId(socialId).orElse(null);

        if (kakaoUser == null) {
            // 카카오 사용자 email 동일한 email 가진 회원이 있는지 확인
            String kakaoEmail = kakaoUserDto.getEmail();
            User sameEmailUser = userRepository.findByEmail(kakaoEmail).orElse(null);

            if (sameEmailUser != null) {
                kakaoUser = sameEmailUser;
                // 기존 회원정보에 카카오 Id, 로그인타입 변경
                kakaoUser = kakaoUser.updateSocialIdAndLoginType(socialId, LoginType.SOCIAL);

            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                kakaoUser = new User(kakaoUserDto.getNickname(),
                                    kakaoEmail,
                                    socialId,
                                    encodedPassword,
                                    kakaoUserDto.getUserRole(),
                                    kakaoUserDto.getLoginType());
            }
            userRepository.save(kakaoUser);
        }
        return kakaoUser;
    }


    /**
     * 소셜로그인한 유저의 추가정보를 업데이트하는 메서드
     * @param authUser
     * @param updateKakaoUserInfoRequest
     * @return 업데이트한 정보를 보여주는 responseDto
     */
    @Transactional
    public UpdateKakaoUserInfoResponse updateKakaoUserInfo (AuthUser authUser, UpdateKakaoUserInfoRequest updateKakaoUserInfoRequest) {
        User kakaoUser = userRepository.findById(authUser.getId())
                .orElseThrow(()-> new ApiException(ErrorStatus._NOT_FOUND_USER));

        // 카카오 로그인한 사람이 아닐 경우 예외 처리
        if (kakaoUser.getLoginType() != LoginType.SOCIAL) {
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }

        kakaoUser.updateUserInfo(updateKakaoUserInfoRequest.getUsername(),
                            updateKakaoUserInfoRequest.getPhoneNumber(),
                            updateKakaoUserInfoRequest.getAddress());

        userRepository.save(kakaoUser);
        return UpdateKakaoUserInfoResponse.of(kakaoUser);
    }
}
