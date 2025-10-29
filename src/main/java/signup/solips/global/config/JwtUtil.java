package signup.solips.global.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT 토큰 유틸리티
 *
 * <p>Access Token과 Refresh Token 생성, 검증, 클레임 추출 기능을 제공합니다.
 *
 * <p>주요 기능:
 * <ul>
 *   <li>Access/Refresh 토큰 생성</li>
 *   <li>토큰 유효성 검사</li>
 *   <li>토큰에서 사용자 ID 추출</li>
 *   <li>토큰 만료 확인</li>
 *   <li>Refresh Token 만료 시간 계산</li>
 * </ul>
 *
 * @author Jeongjunyun777
 */

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;


    public String generateAcessToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userId, accessTokenExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject, Long accessTokenExpiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime()+accessTokenExpiration*1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String getUserIdFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public Date getExpirationDateFromToken(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    private boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public LocalDateTime getRefreshTokenExpiresAt() {
        Date expiryDate = new Date(System.currentTimeMillis() + refreshTokenExpiration);
        return expiryDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public Long getAccessTokenExpirationInSeconds() {
        return accessTokenExpiration / 1000;
    }

    public Long getRefreshTokenExpiration() {
        return  refreshTokenExpiration;
    }

    public String generateRefreshToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userId, refreshTokenExpiration);
    }


}
