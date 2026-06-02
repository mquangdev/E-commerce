package e_commerce.inventory_service.repository;

import e_commerce.inventory_service.entity.InventoryReservationEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservationEntity, UUID> {
  Optional<InventoryReservationEntity> findByInventoryIdAndOrderId(UUID inventoryId, UUID orderId);
  Optional<InventoryReservationEntity> findByOrderIdAndInventoryProductId(UUID orderId, UUID productId);
}
