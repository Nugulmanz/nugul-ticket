package io.nugulticket.config;

public class RedisKeyUtil {
    public static String getSearchKeywordKey(Long userId) {
        return "user:" + userId + ":search";
    }
}
