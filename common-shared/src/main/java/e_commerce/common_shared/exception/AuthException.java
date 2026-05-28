package e_commerce.common_shared.exception;

// Kế thừa RuntimeException để không bắt buộc phải thêm throws ở mọi hàm gọi nó
public class AuthException extends RuntimeException {
  public AuthException(String message) {
    super(message);
  }
}
