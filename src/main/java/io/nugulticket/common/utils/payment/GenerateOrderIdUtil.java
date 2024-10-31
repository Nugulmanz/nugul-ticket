package io.nugulticket.common.utils.payment;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class GenerateOrderIdUtil {
    private final int MIN_LENGTH = 6;
    private final int MAX_LENGTH = 64;

    private final Random rand = new Random();

    public String generateOrderId() {
        return RandomStringUtils.randomAlphanumeric(rand.nextInt(MIN_LENGTH, MAX_LENGTH));
    }
}
