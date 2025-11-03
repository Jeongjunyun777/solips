package signup.solips.users.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import signup.solips.global.config.JwtUtil;
import signup.solips.global.exception.CustomException;
import signup.solips.global.exception.ErrorCode;
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

    /**
     * 회원가입 처리
     *
     * <p>이메일과 사용자 아이디 중복을 체크하고, 비밀번호를 암호화한 후 사용자 정보를 DB에 저장합니다.
     *
     * @param request 회원가입 요청 DTO
     * @return 생성된 사용자 정보(UserInfo)
     * @throws CustomException 이메일 또는 아이디가 이미 존재할 경우 발생
     */

    public UserInfo signup(UserSignupRequestDto request) throws IllegalAccessException {
        log.info("회원가입 시도: userId={}, email={}", request.getUserId(), request.getEmail());

        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if(userRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_USER_ID);
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

    /**
     * 로그인 처리
     *
     * <p>사용자 아이디와 비밀번호를 검증하고, 유효하면 액세스 토큰과 리프레시 토큰을 발급합니다.
     *
     * @param request 로그인 요청 DTO
     * @return 로그인 응답 DTO(UserLoginResponseDto) - 액세스 토큰, 리프레시 토큰, 사용자 정보 포함
     * @throws CustomException 아이디 또는 비밀번호가 잘못된 경우 발생
     */

    @Override
    @Transactional
    public UserLoginResponseDto login(UserLoginRequestDto request) {
        UserEntity user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
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

    /**
     * 로그아웃 처리
     *
     * <p>사용자의 리프레시 토큰을 DB에서 삭제하여 로그아웃 처리합니다.
     *
     * @param userId 로그아웃할 사용자 아이디
     */

    @Override
    public void logout(String userId) {
        userRepository.clearRefreshToken(userId);
    }

    /**
     * 리프레시 토큰을 이용한 액세스 토큰 갱신
     *
     * <p>리프레시 토큰의 유효성 및 만료를 확인하고, 새로운 액세스 토큰을 발급합니다.
     *
     * @param refreshToken 클라이언트가 제공한 리프레시 토큰
     * @return 새로운 액세스 토큰 정보(TokenResponse)
     * @throws CustomException 리프레시 토큰이 유효하지 않거나 만료된 경우 발생
     */

    @Override
    public TokenResponse refreshToken(String refreshToken) throws CustomException {
        if (!jwtUtil.validateToken(refreshToken)) {
            try {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            } catch (CustomException e) {
                throw new RuntimeException(e);
            }
        }
        UserEntity user  = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (user.getRefreshTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        String newAccessToken = jwtUtil.generateAcessToken(user.getUserId());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenExpiration()/1000)
                .build();
    }
}

