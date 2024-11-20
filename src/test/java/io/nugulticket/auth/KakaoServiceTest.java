package io.nugulticket.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nugulticket.auth.dto.kakaoLogin.KakaoLoginResponse;
import io.nugulticket.auth.dto.updateKakaoUserInfo.UpdateKakaoUserInfoRequest;
import io.nugulticket.auth.dto.updateKakaoUserInfo.UpdateKakaoUserInfoResponse;
import io.nugulticket.auth.service.KakaoService;
import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.common.utils.JwtUtil;
import io.nugulticket.user.dto.KakaoUserDto;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.enums.LoginType;
import io.nugulticket.user.enums.UserRole;
import io.nugulticket.user.repository.UserRepository;
import io.nugulticket.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class KakaoServiceTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private KakaoService kakaoService;

    private static final String CLIENT_ID = "testClientId";
    private static final String KAKAO_ACCESS_TOKEN = "testAccessToken";
    private static final String KAKAO_CODE = "testCode";

    @Test
    public void testKakaoLogin_Success() throws JsonProcessingException {
        // Given
        String token = "rgtdfljsdklfjssdfjsdfhdfjhsdjkfdsfsdfsfsdfsfdsdffd";
        User user = new User();
        String responseString = "{\"access_token\":\"" + KAKAO_ACCESS_TOKEN + "\"}";
        String responseString2 = "{\"access_token\":\"" + KAKAO_ACCESS_TOKEN + "\",\"id\": \"123456789\",\"properties\": {\"nickname\": \"nickName\"},\"kakao_account\": {\"email\": \"test@naver.com\"}}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseString2, HttpStatus.OK);

        KakaoUserDto kakaoUserDto = new KakaoUserDto(123456789L, "testNickname", "test@example.com");
        kakaoUserDto.setUserRole(UserRole.USER);
        kakaoUserDto.setLoginType(LoginType.SOCIAL);

        Mockito.when(userRepository.findBySocialId(any())).thenReturn(Optional.empty());

        // Mocking Kakao API responses
        Mockito.when(restTemplate.exchange(any(), eq(String.class))).thenReturn(responseEntity);
        Mockito.when(userRepository.findBySocialId(any())).thenReturn(Optional.of(user));
        Mockito.when(jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole())).thenReturn(token);

        // When
        KakaoLoginResponse response = kakaoService.kakaoLogin(KAKAO_CODE);

        // Then
        assertEquals(token, response.getBearerToken());
        //verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testGetToken_Success() throws JsonProcessingException {
        // Given
        String responseString = "{\"access_token\":\"" + KAKAO_ACCESS_TOKEN + "\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseString, HttpStatus.OK);

        Mockito.when(restTemplate.exchange(any(RequestEntity.class), eq(String.class))).thenReturn(responseEntity);

        // When
        String accessToken = kakaoService.getToken(KAKAO_CODE);

        // Then
        assertEquals(KAKAO_ACCESS_TOKEN, accessToken);
    }

    @Test
    public void testGetKakaoUserInfo_Success() throws JsonProcessingException {
        // Given
        String responseString = "{\"id\":12345,\"properties\":{\"nickname\":\"testNickname\"},\"kakao_account\":{\"email\":\"test@example.com\"}}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseString, HttpStatus.OK);

        Mockito.when(restTemplate.exchange(any(RequestEntity.class), eq(String.class))).thenReturn(responseEntity);

        // When
        KakaoUserDto kakaoUserDto = kakaoService.getKakaoUserInfo(KAKAO_ACCESS_TOKEN);

        // Then
        assertEquals(12345L, kakaoUserDto.getId());
        assertEquals("testNickname", kakaoUserDto.getNickname());
        assertEquals("test@example.com", kakaoUserDto.getEmail());
    }

    @Test
    public void testRegisterKakaoUserIfNeeded_NewUser() {
        // Given
        KakaoUserDto kakaoUserDto = new KakaoUserDto(12345L, "testNickname", "test@example.com");
        kakaoUserDto.setUserRole(UserRole.USER);
        kakaoUserDto.setLoginType(LoginType.SOCIAL);

        Mockito.when(userRepository.findBySocialId(kakaoUserDto.getId())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(kakaoUserDto.getEmail())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When
        User newUser = kakaoService.registerKakaoUserIfNeeded(kakaoUserDto);

        // Then
        assertNotNull(newUser);
        assertEquals("testNickname", newUser.getNickname());
        assertEquals("test@example.com", newUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterKakaoUserIfNeeded_AlreadyUser() {
        // Given
        KakaoUserDto kakaoUserDto = new KakaoUserDto(12345L, "testNickname", "test@example.com");
        kakaoUserDto.setUserRole(UserRole.USER);
        kakaoUserDto.setLoginType(LoginType.SOCIAL);

        User user = TestUtil.getUser(1L);

        Mockito.when(userRepository.findBySocialId(kakaoUserDto.getId())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(kakaoUserDto.getEmail())).thenReturn(Optional.of(user));

        // When
        User newUser = kakaoService.registerKakaoUserIfNeeded(kakaoUserDto);

        // Then
        assertNotNull(newUser);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void test_updateKakaoUserInfo_NotFound() {
        // Given
        UpdateKakaoUserInfoRequest updateKakaoUserInfoRequest = new UpdateKakaoUserInfoRequest("Kim", "testNickname", "test@example.com");
        User user = TestUtil.getUser(1L);
        AuthUser authUser = TestUtil.getAuthUser(user);

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () ->
                kakaoService.updateKakaoUserInfo(authUser, updateKakaoUserInfoRequest));

        assertEquals(exception.getErrorCode(), ErrorStatus._NOT_FOUND_USER);
    }

    @Test
    public void test_updateKakaoUserInfo_PermissionDenied() {
        // Given
        UpdateKakaoUserInfoRequest updateKakaoUserInfoRequest = new UpdateKakaoUserInfoRequest("Kim", "testNickname", "test@example.com");
        User user = TestUtil.getUser(1L);
        AuthUser authUser = TestUtil.getAuthUser(user);

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        ApiException exception = assertThrows(ApiException.class, () ->
                kakaoService.updateKakaoUserInfo(authUser, updateKakaoUserInfoRequest));

        assertEquals(exception.getErrorCode(), ErrorStatus._PERMISSION_DENIED);
    }

    @Test
    public void test_updateKakaoUserInfo() {
        // Given
        UpdateKakaoUserInfoRequest updateKakaoUserInfoRequest = new UpdateKakaoUserInfoRequest("Kim", "testNickname", "test@example.com");
        User user = TestUtil.getUserIdAndSocial(1L);
        AuthUser authUser = TestUtil.getAuthUser(user);

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UpdateKakaoUserInfoResponse updateKakaoUserInfoResponse = kakaoService.updateKakaoUserInfo(authUser, updateKakaoUserInfoRequest);

        // Then
        assertNotNull(updateKakaoUserInfoResponse);
        verify(userRepository, times(1)).save(any(User.class));
    }
}