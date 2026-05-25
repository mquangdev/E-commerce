package e_commerce.catalog_service.commands.repositories;

import e_commerce.catalog_service.commands.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
  boolean existsBySku(String sku);
  Optional<ProductEntity> findByIdAndIsDeletedFalse(UUID id);
}
