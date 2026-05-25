package e_commerce.catalog_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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
}
