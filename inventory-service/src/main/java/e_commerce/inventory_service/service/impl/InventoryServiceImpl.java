package e_commerce.inventory_service.service.impl;

import e_commerce.common_shared.exception.ResourceNotFoundException;
import e_commerce.inventory_service.dto.InventoryRequest;
import e_commerce.inventory_service.dto.InventoryResponse;
import e_commerce.inventory_service.entity.InventoryEntity;
import e_commerce.inventory_service.repository.InventoryRepository;
import e_commerce.inventory_service.service.InventoryService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

  private final InventoryRepository inventoryRepository;

  @Override
  @Transactional
  public InventoryResponse createOrUpdateInventory(InventoryRequest request) {
    InventoryEntity inventory = inventoryRepository.findByProductId(request.getProductId())
        .orElseGet(() -> InventoryEntity.builder()
            .productId(request.getProductId())
            .availableQuantity(0)
            .reservedQuantity(0)
            .build());

    inventory.setAvailableQuantity(inventory.getAvailableQuantity() + request.getQuantity());
    InventoryEntity saved = inventoryRepository.save(inventory);
    return mapToInventoryResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public InventoryResponse getInventoryByProductId(UUID productId) {
    InventoryEntity inventory = inventoryRepository.findByProductId(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tồn kho cho sản phẩm: " + productId));
    return mapToInventoryResponse(inventory);
  }

  private InventoryResponse mapToInventoryResponse(InventoryEntity entity) {
    return InventoryResponse.builder()
        .id(entity.getId())
        .productId(entity.getProductId())
        .availableQuantity(entity.getAvailableQuantity())
        .reservedQuantity(entity.getReservedQuantity())
        .version(entity.getVersion())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .build();
  }
}
