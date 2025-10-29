package signup.solips.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 로그인 요청 DTO
 *
 * <p>사용자 이메일, 아이디, 비밀번호를 전달받아 로그인 인증에 사용됩니다.
 *
 * 필드:
 * <ul>
 *   <li>email - 로그인 이메일</li>
 *   <li>userId - 로그인 아이디</li>
 *   <li>password - 로그인 비밀번호</li>
 * </ul>
 *
 * 유효성:
 * <ul>
 *   <li>NotBlank 필수</li>
 * </ul>
 *
 * @author Jeongjunyun777
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequestDto {
    @NotBlank
    private String email;
    @NotBlank(message = "아이디는 필수입니다.")
    private String userId;
    @NotBlank(message = "비밀번호는 필수 입니다.")
    private String password;
}
