package io.nugulticket.user;

import com.github.javafaker.Faker;
import io.nugulticket.common.utils.JwtUtil;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DummyDataFactory {

    @Autowired
    JwtUtil util;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DummyUserFactory dummyUserFactory;

    private final Faker faker = new Faker();

    @Transactional
    public List<User> createDummyUser(int size) {
        List<User> users = dummyUserFactory.createDummyUser(faker, size);

        return userRepository.saveAll(users);
    }

    @Transactional(readOnly = true)
    public String getTokens() {
        List<User> users = userRepository.findAll();
        StringBuilder result = new StringBuilder();

        for (User user : users) {
            String str = util.createToken(user.getId(), user.getEmail(), user.getUserRole());

            result.append(str).append("\n");
        }

        return result.toString();
    }
}
