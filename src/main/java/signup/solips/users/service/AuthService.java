package signup.solips.users.service;

import signup.solips.users.dto.request.UserLoginRequestDto;
import signup.solips.users.dto.request.UserSignupRequestDto;
import signup.solips.users.dto.response.TokenResponse;
import signup.solips.users.dto.response.UserLoginResponseDto;
import signup.solips.users.dto.response.UserInfo;

/**
 * 인증 서비스 인터페이스
 *
 * <p>회원가입, 로그인, 로그아웃, 토큰 갱신, 아이디 중복 체크 기능을 정의합니다.
 *
 * 메서드:
 * <ul>
 *   <li>signup - 신규 회원가입</li>
 *   <li>login - 로그인 및 토큰 발급</li>
 *   <li>logout - 리프레시 토큰 삭제</li>
 *   <li>refreshToken - 리프레시 토큰으로 새로운 액세스 토큰 발급</li>
 *   <li>isUserIdAvailable - 사용자 ID 중복 여부 확인</li>
 * </ul>
 *
 * @author Jeongjunyun777
 */


public interface AuthService {
    // 회원가입
    UserInfo signup(UserSignupRequestDto request) throws IllegalAccessException;

    // 로그인
    UserLoginResponseDto login(UserLoginRequestDto request);

    // 로그아웃
    void logout(String userId);

    // 토큰 갱신
    TokenResponse refreshToken(String refreshToken) throws IllegalAccessException;

    // 아이디 중복 체크
    boolean isUserIdAvailable(String userId);
}

