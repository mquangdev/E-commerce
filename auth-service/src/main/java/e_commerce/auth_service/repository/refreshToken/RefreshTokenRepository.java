package e_commerce.auth_service.repository.refreshToken;

import e_commerce.auth_service.entity.RefreshTokenEntity;
import e_commerce.auth_service.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

  Optional<RefreshTokenEntity> findByToken(String token);

  Optional<RefreshTokenEntity> findByUserAndDeviceIdAndIsRevokedFalse(
      UserEntity user, String deviceId);

  void deleteByUser(UserEntity user);

  @Modifying
  @Transactional
  @Query(
      value = "DELETE FROM refresh_tokens WHERE expires_at < CURRENT_TIMESTAMP",
      nativeQuery = true)
  void deleteExpiredTokens();

  default void cleanAndSaveNewToken(RefreshTokenEntity newToken, UserEntity user) {
    deleteByUser(user);
    save(newToken);
  }
}
