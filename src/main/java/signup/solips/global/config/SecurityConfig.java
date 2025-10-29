package signup.solips.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import signup.solips.global.security.JwtAuthenticationFilter;

/**
 * Spring Security 설정
 *
 * <p>JWT 인증 기반으로 시큐리티 필터와 인증 정책을 설정합니다.
 *
 * <p>주요 기능:
 * <ul>
 *   <li>BCryptPasswordEncoder 빈 등록</li>
 *   <li>CSRF 비활성화</li>
 *   <li>세션 상태 Stateless로 설정</li>
 *   <li>인증 불필요 경로 설정 ("/solips/auth/**", "/error")</li>
 *   <li>JWT 필터({@link JwtAuthenticationFilter}) 등록</li>
 * </ul>
 *
 * @author Jeongjunyun777
 */

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/solips/auth/signup",
                                "/solips/auth/login",
                                "/solips/auth/logout",
                                "/solips/auth/refresh",
                                "/solips/auth/check-userid",
                                "/error"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
