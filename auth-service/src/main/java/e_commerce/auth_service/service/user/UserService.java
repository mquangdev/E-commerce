package e_commerce.auth_service.service.user;

import e_commerce.auth_service.entity.UserEntity;
import e_commerce.auth_service.repository.user.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;

  public Optional<UserEntity> findById(UUID id) {
    return userRepository.findById(id);
  }

  public UserEntity save(UserEntity user) {
    return userRepository.save(user);
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
