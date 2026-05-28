package e_commerce.auth_service.service.role;

import e_commerce.auth_service.dto.response.PageResponse;
import e_commerce.auth_service.dto.response.RoleResponse;
import e_commerce.auth_service.entity.RoleEntity;
import e_commerce.auth_service.repository.role.RoleRepository;
import e_commerce.common_shared.exception.ResourceAlreadyExistsException;
import e_commerce.common_shared.exception.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

  public RoleResponse getRoleResponseById(UUID id) {
    return mapToResponse(getRoleById(id));
  }

  public PageResponse<RoleResponse> getAllRoles(int page, int size) {
    Page<RoleEntity> rolePage = roleRepository.findAll(PageRequest.of(page, size));
    List<RoleResponse> roleResponses =
        rolePage.getContent().stream().map(this::mapToResponse).collect(Collectors.toList());

    return PageResponse.<RoleResponse>builder()
        .data(roleResponses)
        .totalElements(rolePage.getTotalElements())
        .totalPages(rolePage.getTotalPages())
        .build();
  }

  public RoleResponse createRole(e_commerce.auth_service.dto.request.CreateRoleRequest request) {
    if (roleRepository.existsByName(request.getName())) {
      throw new ResourceAlreadyExistsException("Role với tên " + request.getName() + " đã tồn tại");
    }

    RoleEntity role =
        RoleEntity.builder()
            .id(UUID.randomUUID())
            .name(request.getName())
            .description(request.getDescription())
            .build();

    roleRepository.save(role);
    return mapToResponse(role);
  }

  public RoleResponse updateRole(
      UUID id, e_commerce.auth_service.dto.request.UpdateRoleRequest request) {
    RoleEntity role = getRoleById(id);

    if (!role.getName().equals(request.getName())
        && roleRepository.existsByName(request.getName())) {
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
