package signup.solips.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import signup.solips.global.config.JwtUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT 인증 필터
 *
 * <p>모든 요청에서 JWT를 확인하고, 유효한 경우
 * Spring Security 컨텍스트에 인증 정보를 설정합니다.
 *
 * <p>특정 인증 불필요 경로("/solips/auth/**")는 필터를 스킵합니다.
 *
 * <p>주요 기능:
 * <ul>
 *   <li>인증 불필요 경로 필터 스킵</li>
 *   <li>Authorization 헤더에서 Bearer 토큰 추출</li>
 *   <li>토큰 유효성 검증 후 SecurityContext에 인증 정보 설정</li>
 * </ul>
 *
 * @author Jeongjunyun777
 */



@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/solips/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");

        String token = null;
        String userId = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);

            try {
                userId = jwtUtil.getUserIdFromToken(token);
            } catch (Exception e) {
                logger.error("JWT 토큰 파싱 실패", e);
            }
        }

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(token)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                new ArrayList<>()
                        );

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}