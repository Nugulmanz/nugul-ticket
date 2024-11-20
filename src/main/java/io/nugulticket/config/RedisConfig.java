package io.nugulticket.config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        // Key serializer
        template.setKeySerializer(new StringRedisSerializer());
        // Value serializer
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        //String 형식으로 키,값 직렬화 설정
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());


        return template;
    }
//
//    @Bean
//    public RedissonClient redissonClient() {
//        Config config = new Config();
//        config.useSingleServer()
//                .setAddress("redis://" + host + ":" + port) // 호스트와 포트 동적으로 설정
//                .setConnectionPoolSize(64)
//                .setConnectionMinimumIdleSize(10)
//                .setTimeout(3000)
//                .setRetryAttempts(3)
//                .setRetryInterval(1500);
//        return Redisson.create(config);
//    }
}