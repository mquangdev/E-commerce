package e_commerce.auth_service.service.jwt;

import e_commerce.auth_service.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${jwt.secret}")
  private String secretKey;

  private final long jwtExpiration = 900000; // 15 minutes (accessToken)

  // Trích xuất Username từ Token
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  // Trích xuất một thông tin bất kỳ (Claim) từ Token
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  // Tạo Access Token mới cho User
  public String generateToken(UserEntity userEntity) {
    Map<String, Object> extraClaims = new HashMap<>();

    List<Map<String, Object>> roles =
        userEntity.getRoles().stream()
            .map(
                role -> {
                  Map<String, Object> roleData = new HashMap<>();
                  roleData.put("id", role.getId().toString()); // Chuyển UUID sang String
                  roleData.put("name", role.getName());
                  return roleData;
                })
            .toList();

    extraClaims.put("userId", userEntity.getId().toString());
    extraClaims.put("roles", roles);

    return generateToken(extraClaims, userEntity);
  }

  private String generateToken(Map<String, Object> extraClaims, UserEntity userEntity) {
    return Jwts.builder()
        .claims(extraClaims)
        .subject(userEntity.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
        .signWith(getSignInKey()) // Thuật toán tự động nhận diện từ key (HS256)
        .compact();
  }

  // Kiểm tra Token có hợp lệ và khớp với User không
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date());
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
