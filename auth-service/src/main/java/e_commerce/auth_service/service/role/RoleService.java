package e_commerce.auth_service.service.role;

import e_commerce.auth_service.entity.RoleEntity;
import e_commerce.auth_service.repository.role.RoleRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

  private final RoleRepository roleRepository;

  public Optional<RoleEntity> findById(UUID id) {
    return roleRepository.findById(id);
  }

  public RoleEntity save(RoleEntity role) {
    return roleRepository.save(role);
  }
}
