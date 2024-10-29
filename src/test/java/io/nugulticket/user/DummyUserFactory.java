package io.nugulticket.user;

import com.github.javafaker.Faker;
import io.nugulticket.auth.dto.SignupRequest;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.enums.LoginType;
import io.nugulticket.user.enums.UserRole;
import io.nugulticket.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DummyUserFactory {

    /**
     * Random한 User 데이터를 만들어주는 메서드
     * @param size DummyData 개수
     * @return 완성된 User dummyData
     */
    public List<User> createDummyUser(Faker faker, int size) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        List<User> users = new ArrayList<User>();

        for (int i = 0; i < size; i++) {
            String randomPassword = faker.letterify("?????????");
            String encodedPassword = passwordEncoder.encode(randomPassword);

            SignupRequest signupRequest = new SignupRequest(faker.internet().emailAddress(),
                    encodedPassword,
                    faker.name().name(),
                    faker.funnyName().name(),
                    faker.phoneNumber().phoneNumber(),
                    "adminKey");

            User user = new User(
                    signupRequest.getEmail(),
                    encodedPassword,
                    signupRequest.getName(),
                    signupRequest.getNickname(),
                    signupRequest.getPhoneNumber(),
                    UserRole.ADMIN,
                    LoginType.LOCAL
            );

            users.add(user);
        }

        return users;
    }
}
