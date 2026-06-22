package e_commerce.catalog_service.commands.repositories;

import e_commerce.catalog_service.commands.entities.ProductEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
  boolean existsBySku(String sku);

  Optional<ProductEntity> findByIdAndIsDeletedFalse(UUID id);

  List<ProductEntity> findAllByIdInAndIsDeletedFalse(List<UUID> ids);
}
