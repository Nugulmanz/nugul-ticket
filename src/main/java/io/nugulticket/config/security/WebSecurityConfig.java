package io.nugulticket.config.security;

import io.nugulticket.common.utils.JwtUtil;
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
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;  // JwtUtil 주입

    // JwtSecurityFilter를 수동으로 빈으로 등록
    @Bean
    public JwtSecurityFilter jwtSecurityFilter() {
        return new JwtSecurityFilter(jwtUtil);
    }

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
                .addFilterBefore(jwtSecurityFilter(), SecurityContextHolderAwareRequestFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("**").permitAll() // 테스트용
                        .requestMatchers("/api/auth/v1/**", "/api/search/v1/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()    // 모니터링을 위한 actuator 허용
                        .requestMatchers(HttpMethod.GET, "/api/events/v1/**").permitAll()   // 공연 단건 및 전체 조회
                        .requestMatchers("/api/admin/v1/**").hasAuthority(UserRole.ADMIN.toString())
                        .requestMatchers(HttpMethod.DELETE, "/api/events/v1/**").hasAuthority(UserRole.ADMIN.toString()) // 공연 삭제
                        .requestMatchers("/api/seller/v1/**").hasAuthority(UserRole.SELLER.toString())
                        .requestMatchers(HttpMethod.POST, "/api/events/v1/**").hasAuthority(UserRole.SELLER.toString())  // 공연 생성
                        .requestMatchers(HttpMethod.PATCH, "/api/events/v1/**").hasAuthority(UserRole.SELLER.toString()) // 공연 수정
                        .requestMatchers(HttpMethod.POST, "/api/email/verify-code").hasAuthority(UserRole.UNVERIFIED_USER.toString()) // 이메일 인증 코드 검증은 UNVERIFIED_USER 권한을 가진 사용자 허용
                        .anyRequest().authenticated()
                )
                .build();
    }
}
