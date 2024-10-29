package io.nugulticket;

import io.nugulticket.common.utils.JwtUtil;
import io.nugulticket.user.DummyDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CreateDummyDataTest {
    @Autowired
    DummyDataFactory dummyDataFactory;

    @Autowired
    JwtUtil jwtUtil;

    @Test
    void contextLoads() {
    }

    @Test
    void createDummyUsers() {
        int userSize = 100;
        dummyDataFactory.createDummyUser(userSize);
    }

    @Test
    void getToken() {
        System.out.println(dummyDataFactory.getTokens());
    }
}
