package e_commerce.catalog_service.commands.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryImportDto {
    private int rowNum;

    @NotBlank(message = "Tên không được để trống")
    private String name;

    private String description;

    // Regex ^(?i)(true|false)$ cho phép true/false không phân biệt hoa thường
    @Pattern(regexp = "^(?i)(true|false)$", message = "Cột trạng thái phải là TRUE hoặc FALSE")
    private String statusStr;
}
