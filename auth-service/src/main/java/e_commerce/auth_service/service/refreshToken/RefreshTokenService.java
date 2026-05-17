package e_commerce.auth_service.service.refreshToken;

import e_commerce.auth_service.entity.RefreshTokenEntity;
import e_commerce.auth_service.repository.refreshToken.RefreshTokenRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;

  public Optional<RefreshTokenEntity> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  public RefreshTokenEntity save(RefreshTokenEntity refreshToken) {
    return refreshTokenRepository.save(refreshToken);
  }
}
