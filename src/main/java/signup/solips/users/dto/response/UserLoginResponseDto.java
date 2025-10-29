package signup.solips.users.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 로그인 응답 DTO
 *
 * <p>로그인 성공 시 클라이언트에 반환되는 토큰과 사용자 정보입니다.
 *
 * 필드:
 * <ul>
 *   <li>accessToken - 발급된 액세스 토큰</li>
 *   <li>refreshToken - 발급된 리프레시 토큰</li>
 *   <li>tokenType - 토큰 타입 (예: Bearer)</li>
 *   <li>expiresIn - 액세스 토큰 만료 시간(초)</li>
 *   <li>user - {@link UserInfo} 사용자 정보</li>
 * </ul>
 *
 * @author Jeongjunyun777
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginResponseDto {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserInfo user;

}
