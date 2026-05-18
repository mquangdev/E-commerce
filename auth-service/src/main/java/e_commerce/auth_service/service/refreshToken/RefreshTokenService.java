package e_commerce.auth_service.service.refreshToken;

import e_commerce.auth_service.entity.RefreshTokenEntity;
import e_commerce.auth_service.repository.refreshToken.RefreshTokenRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
}
