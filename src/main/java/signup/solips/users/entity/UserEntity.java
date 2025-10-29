package signup.solips.users.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 사용자 엔티티
 *
 * <p>회원 정보를 데이터베이스에 저장하기 위한 JPA 엔티티입니다.
 *
 * 필드:
 * <ul>
 *   <li>id - PK, 자동 생성</li>
 *   <li>email - 고유 이메일</li>
 *   <li>userId - 회원 아이디</li>
 *   <li>password - 암호화된 비밀번호</li>
 *   <li>refreshToken - 최신 리프레시 토큰</li>
 *   <li>refreshTokenExpiresAt - 리프레시 토큰 만료 시간</li>
 *   <li>createdAt - 계정 생성 시간</li>
 * </ul>
 *
 * @author Jeongjunyun777
 */


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false,length = 100)
    private String email;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(nullable = false,length = 100)
    private String password;

    @Column(name = "refresh_token", nullable = true, length = 500)
    private String refreshToken;

    @Column(name = "refresh_token_expires_at", nullable = true)
    private LocalDateTime refreshTokenExpiresAt;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
