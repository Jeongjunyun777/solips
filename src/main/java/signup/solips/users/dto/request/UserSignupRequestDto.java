package signup.solips.users.dto.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * 회원가입 요청 DTO
 *
 * <p>사용자 회원가입 정보를 전달받아 신규 계정을 생성할 때 사용됩니다.
 *
 * 필드:
 * <ul>
 *   <li>email - 학교 이메일 (s00000@gsm.hs.kr 형식)</li>
 *   <li>userId - 회원 아이디</li>
 *   <li>password - 비밀번호 (대소문자, 숫자, 특수문자 포함)</li>
 * </ul>
 *
 * 유효성:
 * <ul>
 *   <li>Email 형식 검증</li>
 *   <li>NotBlank 필수</li>
 *   <li>Password 패턴 검증</li>
 * </ul>
 *
 * @author Jeongjunyun777
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignupRequestDto {
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    @Pattern(regexp = "^s\\d{5}@gsm\\.hs\\.kr$",
            message ="이메일 형식은 s00000gsm.hs.kr 입니다." )
    private String email;


    @NotBlank(message = "아이디는 필수 입니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 입니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "비밀번호는 대소문자, 숫자, 특수문자를 모두 포함해야 합니다.")
    private String password;
}

