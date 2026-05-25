package e_commerce.catalog_service.commands.repositories;

import e_commerce.catalog_service.commands.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
  boolean existsByName(String name);
  Optional<CategoryEntity> findByIdAndIsDeletedFalse(UUID id);
}
