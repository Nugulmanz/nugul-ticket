package io.nugulticket.user.service;

import io.nugulticket.user.entity.User;
import io.nugulticket.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    /**
     * 주어진 email로 해당 유저가 이미 존재하는지 확인
     * @param email
     * @return boolean(true=존재함, false=없음)
     */
    public Boolean isUser(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * userId로 해당 유저 조회 및 반환
     * @param userId
     * @return User
     */
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User with id " + userId + " not found")
        );
    }

    /**
     * email로 해당 유저 조회 및 반환
     * @param email
     * @return User
     */
    public User getUserFromEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User with email " + email + " not found")
        );
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }
}
