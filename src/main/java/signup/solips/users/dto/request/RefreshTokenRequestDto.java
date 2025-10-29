package signup.solips.users.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 리프레시 토큰 요청 DTO
 *
 * <p>리프레시 토큰을 전달받아 새로운 Access Token과 Refresh Token을 발급할 때 사용됩니다.
 *
 * 필드:
 * <ul>
 *   <li>refreshToken - 클라이언트가 전달하는 기존 리프레시 토큰</li>
 * </ul>
 *
 * @author Jeongjunyun777
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequestDto {
    private String refreshToken;
}
