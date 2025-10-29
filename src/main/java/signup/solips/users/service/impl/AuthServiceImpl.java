package signup.solips.users.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import signup.solips.global.config.JwtUtil;
import signup.solips.users.dto.request.UserLoginRequestDto;
import signup.solips.users.dto.request.UserSignupRequestDto;
import signup.solips.users.dto.response.TokenResponse;
import signup.solips.users.dto.response.UserLoginResponseDto;
import signup.solips.users.dto.response.UserInfo;
import signup.solips.users.entity.UserEntity;
import signup.solips.users.repository.UserRepository;
import signup.solips.users.service.AuthService;

import java.time.LocalDateTime;

/**
 * 인증 서비스 구현체
 *
 * <p>{@link AuthService}를 구현하며, JWT 토큰 발급과 검증, 사용자 정보 관리 기능을 수행합니다.
 *
 * 주요 기능:
 * <ul>
 *   <li>회원가입 시 이메일/아이디 중복 체크 및 비밀번호 암호화 후 저장</li>
 *   <li>로그인 시 아이디/비밀번호 검증 및 토큰 발급</li>
 *   <li>로그아웃 시 리프레시 토큰 삭제</li>
 *   <li>리프레시 토큰 검증 후 새로운 액세스 토큰 발급</li>
 *   <li>사용자 ID 중복 여부 확인</li>
 * </ul>
 *
 * @author Jeongjunyun777
 */

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    @Override
    public UserInfo signup(UserSignupRequestDto request) throws IllegalAccessException {
        log.info("회원가입 시도: userId={}, email={}", request.getUserId(), request.getEmail());

        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalAccessException("이미 사용중인 이메일입니다.");
        }
        if(userRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new IllegalAccessException("이미 사용 중인 아이디입니다.");
        }

        String password = passwordEncoder.encode(request.getPassword());

        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .userId(request.getUserId())
                .password(password)
                .build();

        UserEntity userEntity = userRepository.save(user);

        log.info("회원가입 성공 : user_id={}, email={}", user.getId(), user.getEmail());

        return new UserInfo(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getUserId()
        );
    }

    @Override
    @Transactional
    public UserLoginResponseDto login(UserLoginRequestDto request) {
        UserEntity user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다");  // ✅
        }

        String accessToken = jwtUtil.generateAcessToken(user.getUserId());

        String  refreshToken = jwtUtil.generateRefreshToken(user.getUserId());
        LocalDateTime expiresAt = jwtUtil.getRefreshTokenExpiresAt();

        userRepository.updateRefreshToken(
                refreshToken,
                user.getUserId(),
                expiresAt
        );
        UserInfo userInfo = new UserInfo(
                user.getId(),
                user.getEmail(),
                user.getUserId());

        return UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenExpirationInSeconds())
                .user(userInfo)
                .build();
    }

    @Override
    public void logout(String userId) {
        userRepository.clearRefreshToken(userId);
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) throws IllegalAccessException {
        if (!jwtUtil.validateToken(refreshToken)) {
            try {
                throw new IllegalAccessException("유효하지않은 리프레시 토큰입니다.");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        UserEntity user  = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지않은 토큰입니다."));

        if (user.getRefreshTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalAccessException("만료된 토큰입니다.");
        }

        String newAccessToken = jwtUtil.generateAcessToken(user.getUserId());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenExpiration()/1000)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserIdAvailable(String userId) {
        return !userRepository.findByUserId(userId).isPresent();
    }
}
