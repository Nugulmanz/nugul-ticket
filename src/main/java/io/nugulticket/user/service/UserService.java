package io.nugulticket.user.service;

import io.nugulticket.user.dto.UserResponse;
import io.nugulticket.user.dto.UserUpdateRequest;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    /**
     * 사용자의 닉네임, 주소를 업데이트 하는 메서드
     * @param userId 사용자 id
     * @param updateRequest 업데이트 내용
     * @return UserResponse
     */
    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest updateRequest){
        User user = userRepository.findUserById(userId);
        user.updateUser(updateRequest.getNickname(), updateRequest.getAddress());
        return UserResponse.from(user);
    }

    /**
     * 주어진 email로 해당 유저가 이미 존재하는지 확인
     * @param email 조회할 email
     * @return boolean(true=존재함, false=없음)
     */
    public Boolean isUser(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * userId로 해당 유저 조회 및 반환
     * @param userId 조회할 userId
     * @return 해당 userId를 갖고 있는 User
     */
    public User getUser(Long userId) {
        return userRepository.findUserById(userId);
    }

    /**
     * email로 해당 유저 조회 및 반환
     * @param email 조회할 이메일
     * @return 해당 이메일을 사용중인 유저 반환
     */
    public User getUserFromEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    /**
     * 유저 정보를 저장하는 메서드
     * @param user 저장할 User 정보
     * @return 저장된 User 정보
     */
    public User addUser(User user) {
        return userRepository.save(user);
    }

    /**
     * email로 해당 유저 조회 및 반환
     * @param email 조회할 이메일
     * @return Optional로 감싸진 해당 이메일을 사용중인 유저 반환
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 유저 권한이 갱신된 유저를 저장하는 메서드
     * @param user 권한이 갱신된 유저
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateUserRole(User user) {
        userRepository.save(user);
        System.out.println("DB에 저장된 UserRole: " + user.getUserRole()); // 디버그 출력
    }

    //Otp 재발송 코드
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
