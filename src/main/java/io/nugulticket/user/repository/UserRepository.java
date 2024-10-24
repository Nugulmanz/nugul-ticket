package io.nugulticket.user.repository;

import io.nugulticket.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findBySocialId(Long socialId);

    default User findUserById(Long id) {
        return findById(id).orElseThrow(
                () -> new RuntimeException("사용자를 찾을 수 없습니다.")
        );
    }

    default User findUserByEmail(String email) {
        return findByEmail(email).orElseThrow(
                () -> new RuntimeException("해당 이메일의 사용자를 찾을 수 없습니다.")
        );
    }
}
