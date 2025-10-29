package signup.solips.users.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import signup.solips.users.dto.request.RefreshTokenRequestDto;
import signup.solips.users.dto.request.UserLoginRequestDto;
import signup.solips.users.dto.request.UserSignupRequestDto;
import signup.solips.users.dto.response.TokenResponse;
import signup.solips.users.dto.response.UserLoginResponseDto;
import signup.solips.users.dto.response.UserInfo;
import signup.solips.users.service.AuthService;
import signup.solips.users.service.impl.AuthServiceImpl;

/**
 * 인증 관련 API 컨트롤러
 *
 * <p>회원가입, 로그인, 로그아웃, 토큰 갱신 등 인증 관련 요청을 처리합니다.
 *
 * <p>주요 기능:
 * <ul>
 *   <li>회원가입</li>
 *   <li>로그인</li>
 *   <li>로그아웃</li>
 *   <li>리프레시 토큰 갱신</li>
 *   <li>사용자 ID/이메일 중복 체크</li>
 * </ul>
 *
 * <p>각 요청은 {@link AuthServiceImpl}에 위임됩니다.
 *
 * @author Jeongjunyun777
 */

@RestController
@RequestMapping("/solips/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;


    /**
     * 회원가입
     *
     * @param request 회원가입 요청 DTO
     * @return 생성된 사용자 정보
     * @throws IllegalAccessException 회원가입 실패 시
     */
    @PostMapping("/signup")
    public ResponseEntity<UserInfo> signup(@Valid @RequestBody UserSignupRequestDto request) throws IllegalAccessException {
        UserInfo userInfo = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userInfo);
    }
    /**
     * 로그인
     *
     * @param request 로그인 요청 DTO
     * @return 로그인 결과(토큰 + 사용자 정보)
     * @throws IllegalAccessException 로그인 실패 시
     */
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@Valid @RequestBody UserLoginRequestDto request) throws IllegalAccessException {
        UserLoginResponseDto userLoginResponseDto = authService.login(request);
        return ResponseEntity.ok(userLoginResponseDto);
    }
    /**
     * 로그아웃
     *
     * @param userId 인증된 사용자 ID
     * @return 빈 응답
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal String userId) {
        authService.logout(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 리프레시 토큰 갱신
     *
     * @param request 리프레시 토큰 요청 DTO
     * @return 새로운 Access/Refresh 토큰
     * @throws IllegalAccessException 토큰 갱신 실패 시
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequestDto request) throws IllegalAccessException {
        TokenResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}
// 중복 체크 응답 Record
record CheckAvailabilityResponse(
        boolean available,
        String message
) {}

