package e_commerce.inventory_service.temporal;

import e_commerce.common_shared.exception.BusinessException;
import e_commerce.inventory_service.dto.OrderItemDTO;
import e_commerce.inventory_service.entity.InventoryEntity;
import e_commerce.inventory_service.repository.InventoryRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryActivitiesImpl implements InventoryActivities {
  private final InventoryRepository inventoryRepository;

  @Override
  @Transactional
  public void reserveStock(UUID orderId, List<OrderItemDTO> items) {
    List<InventoryEntity> updatedInventories = new ArrayList<>();

    for (OrderItemDTO item : items) {
      InventoryEntity inventory =
          inventoryRepository
              .findById(item.getProductId())
              .orElseThrow(
                  () ->
                      new EntityNotFoundException(
                          "Không tìm thấy sản phẩm: " + item.getProductId()));

      if (inventory.getAvailableQuantity() < item.getQuantity()) {
        throw new BusinessException(
            "Số lượng sản phẩm không đủ để đặt hàng: " + item.getProductId());
      }

      inventory.setAvailableQuantity(inventory.getAvailableQuantity() - item.getQuantity());
      inventory.setReservedQuantity(inventory.getReservedQuantity() + item.getQuantity());

      updatedInventories.add(inventory);
    }

    inventoryRepository.saveAll(updatedInventories);
  }

  @Override
  @Transactional
  public void compensateStock(UUID orderId, List<OrderItemDTO> items) {
    List<InventoryEntity> updatedInventories = new ArrayList<>();

    for (OrderItemDTO item : items) {
      // Trong luồng bù trừ (rollback), chúng ta nên dùng ifPresent để tránh ném lỗi
      // làm gián đoạn quá trình cứu vãn dữ liệu của các món hàng khác.
      inventoryRepository
          .findById(item.getProductId())
          .ifPresent(
              inventory -> {
                inventory.setAvailableQuantity(
                    inventory.getAvailableQuantity() + item.getQuantity());
                inventory.setReservedQuantity(inventory.getReservedQuantity() - item.getQuantity());
                updatedInventories.add(inventory);
              });

      inventoryRepository.saveAll(updatedInventories);
    }
  }
}
