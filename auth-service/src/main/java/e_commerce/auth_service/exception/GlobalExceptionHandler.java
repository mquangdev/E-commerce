package e_commerce.auth_service.exception;

import e_commerce.auth_service.dto.response.ErrorResponse;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<ErrorResponse> handleAuthException(AuthException ex) {

    // Đóng gói dữ liệu vào ErrorResponse
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.UNAUTHORIZED.value()) // Mã 401
            .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
            .message(ex.getMessage()) // Lấy câu thông báo từ lúc chúng ta throw
            .build();

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(ResourceAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsException(
      ResourceAlreadyExistsException ex) {

    // Đóng gói dữ liệu vào ErrorResponse
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message(ex.getMessage()) // Lấy câu thông báo từ lúc chúng ta throw
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsException(
      ResourceNotFoundException ex) {

    // Đóng gói dữ liệu vào ErrorResponse
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message(ex.getMessage()) // Lấy câu thông báo từ lúc chúng ta throw
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }
}
