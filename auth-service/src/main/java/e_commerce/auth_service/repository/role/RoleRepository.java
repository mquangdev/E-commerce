package e_commerce.auth_service.repository.role;

import e_commerce.auth_service.entity.RoleEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

  Optional<RoleEntity> findByName(String name);
  
  boolean existsByName(String name);

  List<RoleEntity> findAllByIdIn(Collection<UUID> ids);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM roles WHERE description IS NULL", nativeQuery = true)
  void deleteRolesWithoutDescription();
}
