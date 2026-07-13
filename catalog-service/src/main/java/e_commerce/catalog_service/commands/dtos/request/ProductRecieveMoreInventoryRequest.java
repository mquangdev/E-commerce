package e_commerce.catalog_service.commands.dtos.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRecieveMoreInventoryRequest {
    @Min(value = 0, message = "Số lượng tồn kho nhập thêm không được nhỏ hơn 0")
    private int addedInventory;
}
