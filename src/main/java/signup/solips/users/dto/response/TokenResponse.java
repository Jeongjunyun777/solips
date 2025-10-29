package signup.solips.users.dto.response;

import lombok.*;

/**
 * 토큰 응답 DTO
 *
 * <p>로그인 혹은 리프레시 토큰 발급 후 클라이언트에 반환되는 토큰 정보입니다.
 *
 * 필드:
 * <ul>
 *   <li>accessToken - 발급된 액세스 토큰</li>
 *   <li>tokenType - 토큰 타입 (예: Bearer)</li>
 *   <li>expiresIn - 액세스 토큰 만료 시간(초)</li>
 * </ul>
 *
 * @author Jeongjunyun777
 */


@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
}
