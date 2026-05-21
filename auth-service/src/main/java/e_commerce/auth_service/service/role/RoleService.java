package e_commerce.auth_service.service.role;

import e_commerce.auth_service.dto.response.RoleResponse;
import e_commerce.auth_service.entity.RoleEntity;
import e_commerce.auth_service.exception.ResourceAlreadyExistsException;
import e_commerce.auth_service.exception.ResourceNotFoundException;
import e_commerce.auth_service.repository.role.RoleRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

  private final RoleRepository roleRepository;

  public RoleEntity getRoleById(UUID id) {
    return roleRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Role với ID: " + id));
  }

  public RoleResponse createRole(e_commerce.auth_service.dto.request.CreateRoleRequest request) {
    if (roleRepository.existsByName(request.getName())) {
      throw new ResourceAlreadyExistsException("Role với tên " + request.getName() + " đã tồn tại");
    }

    RoleEntity role = RoleEntity.builder()
        .id(UUID.randomUUID())
        .name(request.getName())
        .description(request.getDescription())
        .build();

    roleRepository.save(role);
    return mapToResponse(role);
  }

  public RoleResponse updateRole(UUID id, e_commerce.auth_service.dto.request.UpdateRoleRequest request) {
    RoleEntity role = getRoleById(id);

    if (!role.getName().equals(request.getName()) && roleRepository.existsByName(request.getName())) {
      throw new ResourceAlreadyExistsException("Role với tên " + request.getName() + " đã tồn tại");
    }

    role.setName(request.getName());
    role.setDescription(request.getDescription());

    roleRepository.save(role);
    return mapToResponse(role);
  }

  public void deleteRole(UUID id) {
    RoleEntity role = getRoleById(id);
    roleRepository.delete(role);
  }

  private RoleResponse mapToResponse(RoleEntity role) {
    return RoleResponse.builder()
        .id(role.getId())
        .name(role.getName())
        .description(role.getDescription())
        .build();
  }
}
