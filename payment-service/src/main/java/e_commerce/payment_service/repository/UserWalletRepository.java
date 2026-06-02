package e_commerce.payment_service.repository;

import e_commerce.payment_service.entity.UserWalletEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWalletRepository extends JpaRepository<UserWalletEntity, UUID> {
  Optional<UserWalletEntity> findByUserId(UUID userId);
}
