package e_commerce.common_shared.exception;

import e_commerce.common_shared.dtos.ErrorResponse;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
  public ResponseEntity<String> handleOptimisticLockingFailure(
      ObjectOptimisticLockingFailureException ex) {

    // 1. Lấy tên class gốc (Ví dụ: "com.ecommerce.catalog.entity.Product")
    String fullEntityName = ex.getPersistentClassName();

    // 2. Cắt lấy chữ cuối cùng để ra tên ngắn gọn (Ví dụ: "Product")
    String entityName = fullEntityName.substring(fullEntityName.lastIndexOf('.') + 1);

    // 3. Ghép vào chuỗi thông báo chung
    String message =
        String.format(
            "Dữ liệu của %s này đã bị thay đổi bởi một tác vụ khác. Vui lòng làm mới trang!",
            entityName);

    // Trả về lỗi 409 Conflict
    return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
  }

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
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
      ResourceNotFoundException ex) {

    // Đóng gói dữ liệu vào ErrorResponse
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error(HttpStatus.NOT_FOUND.getReasonPhrase())
            .message(ex.getMessage()) // Lấy câu thông báo từ lúc chúng ta throw
            .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message(ex.getMessage()) // Trả về câu thông báo nghiệp vụ chi tiết
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {

    // 1. Rút trích tất cả các câu message lỗi (VD: "Email không được để trống")
    String errorMessage =
        ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", ")); // Nếu lỗi nhiều field thì ghép lại bằng dấu phẩy

    // 2. Lắp ráp vào ErrorResponse chuẩn của bạn
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message(errorMessage)
            .build();

    // 3. Trả về cho Client
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }
}
