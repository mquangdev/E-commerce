package e_commerce.inventory_service.service;

import e_commerce.inventory_service.dto.InventoryRequest;
import e_commerce.inventory_service.dto.InventoryResponse;
import java.util.UUID;

public interface InventoryService {
  InventoryResponse createOrUpdateInventory(InventoryRequest request);
  InventoryResponse getInventoryByProductId(UUID productId);
}
