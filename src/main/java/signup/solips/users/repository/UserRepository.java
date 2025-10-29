package signup.solips.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import signup.solips.users.entity.UserEntity;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 사용자 엔티티 Repository
 *
 * <p>Spring Data JPA를 활용하여 UserEntity 조회 및 수정 쿼리를 제공합니다.
 *
 * 주요 기능:
 * <ul>
 *   <li>findByEmail - 이메일로 사용자 조회</li>
 *   <li>findByUserId - 아이디로 사용자 조회</li>
 *   <li>findByRefreshToken - 리프레시 토큰으로 사용자 조회</li>
 *   <li>updateRefreshToken - 리프레시 토큰과 만료 시간 업데이트</li>
 *   <li>clearRefreshToken - 리프레시 토큰 삭제</li>
 * </ul>
 *
 * @author Jeongjunyun777
 */


@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUserId(String userid);
    Optional<UserEntity> findByRefreshToken(String refreshToken);



    @Modifying
    @Query("UPDATE UserEntity u SET u.refreshToken = :token, u.refreshTokenExpiresAt = :expiresAt WHERE u.userId = :userId")
    void updateRefreshToken(@Param("token") String token,
                            @Param("userId") String userId,
                            @Param("expiresAt") LocalDateTime expiresAt);


    @Modifying
    @Transient
    @Query("UPDATE UserEntity u SET u.refreshToken = NULL, u.refreshTokenExpiresAt = NULL WHERE u.userId = :userId")
    void clearRefreshToken(@Param("userId") String userId);


}
