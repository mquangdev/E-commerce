package e_commerce.catalog_service.commands.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ImportResultResponse {
    // Tổng số dòng đã đọc
    private int totalRows;

    // Số dòng lưu DB thành công
    private int successCount;

    // Số dòng bị bỏ qua (Do lỗi định dạng, sang kiểu dữ liệu, trùng lặp...)
    private int errorCount;

    private List<RowErrorDetail> errors;
}
