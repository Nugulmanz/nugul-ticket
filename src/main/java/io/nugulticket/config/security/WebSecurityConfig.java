package io.nugulticket.config.security;

import io.nugulticket.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    private final JwtSecurityFilter jwtSecurityFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // JwtSecurityFilter 에 @Component 를 붙이면 두번 호출되므로 둘 중 하나만 적용해야함! 둘 모두 빈으로 등록하고 있음
//                .addFilterBefore(jwtSecurityFilter, SecurityContextHolderAwareRequestFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/v1/**", "/api/search/v1/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/events/v1/**").permitAll()   // 공연 단건 및 전체 조회
                        .requestMatchers("/api/admin/v1/**").hasAuthority(UserRole.Authority.ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/events/v1/**").hasAuthority(UserRole.Authority.ADMIN) // 공연 삭제
                        .requestMatchers("/api/seller/v1/**").hasAuthority(UserRole.Authority.SELLER)
                        .requestMatchers(HttpMethod.POST, "/api/events/v1/**").hasAuthority(UserRole.Authority.SELLER)  // 공연 생성
                        .requestMatchers(HttpMethod.PATCH, "/api/events/v1/**").hasAuthority(UserRole.Authority.SELLER) // 공연 수정
                        .anyRequest().authenticated()
                )
                .build();
    }
}
