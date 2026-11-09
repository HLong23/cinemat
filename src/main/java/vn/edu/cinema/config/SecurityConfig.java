package vn.edu.cinema.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import vn.edu.cinema.security.JwtAuthenticationFilter;
import vn.edu.cinema.security.JwtService;

import java.io.IOException;

/**
 * Hai filter chain tách biệt:
 *  - Order 1: /api/**  -> stateless, xác thực bằng JWT (Bearer token), trả lỗi JSON.
 *  - Order 2: còn lại  -> giao diện web, form login + HttpSession + remember-me cookie.
 * Phân quyền theo method bằng @PreAuthorize (bật bởi @EnableMethodSecurity) ở tầng service.
 *
 * Dùng AntPathRequestMatcher để tránh lỗi "không phân biệt được MVC pattern" khi bật H2 console.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http, JwtService jwtService,
                                              UserDetailsService userDetailsService) throws Exception {
        http
                .securityMatcher(AntPathRequestMatcher.antMatcher("/api/**"))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/auth/**")).permitAll()
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/movies/**"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/cinemas/**"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/showtimes/**")).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) ->
                                writeJson(res, HttpServletResponse.SC_UNAUTHORIZED, "Chưa đăng nhập hoặc token không hợp lệ"))
                        .accessDeniedHandler((req, res, ex) ->
                                writeJson(res, HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện thao tác này")))
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        http
                .authorizeHttpRequests(a -> a
                        .requestMatchers(ant("/", "/movies/**", "/login", "/register", "/error",
                                "/css/**", "/js/**", "/images/**", "/favicon.ico",
                                "/actuator/health", "/h2-console/**")).permitAll()
                        .requestMatchers(ant("/admin/**")).hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(f -> f
                        .loginPage("/login")
                        .defaultSuccessUrl("/", false)
                        .permitAll())
                .rememberMe(r -> r
                        .key("cinemat-remember-me-key")
                        .userDetailsService(userDetailsService)
                        .tokenValiditySeconds(7 * 24 * 3600))
                .logout(l -> l
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID", "remember-me"))
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")))
                .headers(h -> h.frameOptions(f -> f.sameOrigin()));
        return http.build();
    }

    private static RequestMatcher[] ant(String... patterns) {
        RequestMatcher[] matchers = new RequestMatcher[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            matchers[i] = AntPathRequestMatcher.antMatcher(patterns[i]);
        }
        return matchers;
    }

    private static void writeJson(HttpServletResponse res, int status, String message) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"status\":" + status + ",\"message\":\"" + message + "\"}");
    }
}
