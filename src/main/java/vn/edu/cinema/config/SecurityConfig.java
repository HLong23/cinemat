package main.java.com.cinemat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * TẠM THỜI (Giai đoạn 1): mở toàn bộ request để dev các phần đầu.
 * Sẽ được thay bằng cấu hình JWT + phân quyền ở Giai đoạn 7.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(h -> h.frameOptions(f -> f.sameOrigin())) // cho phép H2 console
            .authorizeHttpRequests(a -> a.anyRequest().permitAll());
        return http.build();
    }
}
