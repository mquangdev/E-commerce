package e_commerce.auth_service.service.user;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import e_commerce.auth_service.dto.request.CreateUserRequest;
import e_commerce.auth_service.dto.request.UpdateUserRequest;
import e_commerce.auth_service.dto.response.RoleResponse;
import e_commerce.auth_service.dto.response.UserResponse;
import e_commerce.auth_service.entity.UserEntity;
import e_commerce.auth_service.enums.UserStatus;
import e_commerce.auth_service.exception.ResourceAlreadyExistsException;
import e_commerce.auth_service.exception.ResourceNotFoundException;
import e_commerce.auth_service.repository.role.RoleRepository;
import e_commerce.auth_service.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  public UserEntity getUserById(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User với ID: " + id));
  }

  public UserResponse createUser(CreateUserRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new ResourceAlreadyExistsException("Username đã tồn tại");
    }
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new ResourceAlreadyExistsException("Email đã tồn tại");
    }

    UserEntity user = UserEntity.builder()
        .id(UUID.randomUUID())
        .username(request.getUsername())
        .email(request.getEmail())
        .passwordHash(passwordEncoder.encode(request.getPassword()))
        .status(request.getStatus() != null ? request.getStatus() : UserStatus.ACTIVE)
        .build();

    if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
      List<e_commerce.auth_service.entity.RoleEntity> roles = roleRepository.findAllByIdIn(request.getRoleIds());
      user.setRoles(new HashSet<>(roles));
    } else {
      user.setRoles(new HashSet<>());
    }

    userRepository.save(user);
    return mapToResponse(user);
  }

  public UserResponse updateUser(UUID id, UpdateUserRequest request) {
    UserEntity user = getUserById(id);

    if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
      throw new ResourceAlreadyExistsException("Email đã tồn tại");
    }

    user.setEmail(request.getEmail());
    if (request.getStatus() != null) {
      user.setStatus(request.getStatus());
    }

    if (request.getRoleIds() != null) {
      List<e_commerce.auth_service.entity.RoleEntity> roles = roleRepository.findAllByIdIn(request.getRoleIds());
      user.setRoles(new HashSet<>(roles));
    }

    userRepository.save(user);
    return mapToResponse(user);
  }

  public void deleteUser(UUID id) {
    UserEntity user = getUserById(id);
    userRepository.delete(user);
  }

  private UserResponse mapToResponse(UserEntity user) {
    return UserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .status(user.getStatus())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .roles(user.getRoles().stream()
            .map(role -> RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .build())
            .collect(Collectors.toSet()))
        .build();
  }

  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // Tìm user trong DB, nếu không có thì ném ra lỗi mặc định của Spring Security
    return userRepository
        .findByUsername(username)
        .orElseThrow(
            () ->
                new UsernameNotFoundException(
                    "Không tìm thấy người dùng với username: " + username));
  }
}
