package signup.solips.users.dto.response;

/**
 * 사용자 정보 응답 DTO
 *
 * <p>회원가입 또는 로그인 시 클라이언트에 반환되는 최소 사용자 정보입니다.
 *
 * 필드:
 * <ul>
 *   <li>id - 사용자 DB 식별자</li>
 *   <li>email - 사용자 이메일</li>
 *   <li>userId - 사용자 아이디</li>
 * </ul>
 *
 * @author Jeongjunyun777
 */


public record UserInfo(
        Long id,
        String email,
        String userId
) {}

