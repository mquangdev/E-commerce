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

  Optional<RefreshTokenEntity> findByToken(UUID token);

  Optional<RefreshTokenEntity> findByUserAndDeviceIdAndIsRevokedFalse(
      UserEntity user, String deviceId);

  void deleteByUser(UserEntity user);

  @Modifying
  @Transactional
  @Query(
          value = "UPDATE RefreshTokenEntity r SET r.isRevoked = true WHERE r.user = :user AND r.isRevoked = false AND r.deviceId = :deviceId"
  )
  void revokeAllTokenByUserAndDeviceId(UserEntity user, String deviceId);

  @Modifying
  @Transactional
  @Query(
      value =
          "UPDATE RefreshTokenEntity r SET r.isRevoked = true WHERE r.user = :user AND r.isRevoked = false")
  void revokeAllTokenByUser(UserEntity user);

  default void cleanAndSaveNewToken(RefreshTokenEntity newToken, UserEntity user) {
    deleteByUser(user);
    save(newToken);
  }
}
