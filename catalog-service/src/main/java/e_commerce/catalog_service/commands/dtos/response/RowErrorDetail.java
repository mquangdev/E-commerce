package e_commerce.catalog_service.commands.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RowErrorDetail {
    private int rowIndex;
    private String errorMessage;
}
