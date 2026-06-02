package e_commerce.inventory_service.repository;

import e_commerce.inventory_service.entity.InventoryEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, UUID> {
  Optional<InventoryEntity> findByProductId(UUID productId);
  boolean existsByProductId(UUID productId);
}
