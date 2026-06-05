package e_commerce.inventory_service.temporal;

import e_commerce.common_shared.exception.BusinessException;
import e_commerce.inventory_service.dto.OrderItemDTO;
import e_commerce.inventory_service.entity.InventoryEntity;
import e_commerce.inventory_service.entity.InventoryReservationEntity;
import e_commerce.inventory_service.enums.ReservationStatus;
import e_commerce.inventory_service.repository.InventoryRepository;
import e_commerce.inventory_service.repository.InventoryReservationRepository;
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
  private final InventoryReservationRepository inventoryReservationRepository;

  @Override
  @Transactional
  public void reserveStock(UUID orderId, List<OrderItemDTO> items) {
    List<InventoryEntity> updatedInventories = new ArrayList<>();
    List<InventoryReservationEntity> reservations = new ArrayList<>();

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

      InventoryReservationEntity reservation =
          InventoryReservationEntity.builder()
              .orderId(orderId)
              .inventory(inventory)
              .reservedQuantity(item.getQuantity())
              .status(ReservationStatus.PENDING)
              .build();

      updatedInventories.add(inventory);
      reservations.add(reservation);
    }

    inventoryRepository.saveAll(updatedInventories);
    inventoryReservationRepository.saveAll(reservations);
  }

  @Override
  @Transactional
  public void compensateStock(UUID orderId, List<OrderItemDTO> items) {
    List<InventoryEntity> updatedInventories = new ArrayList<>();
    List<InventoryReservationEntity> reservations =
        inventoryReservationRepository.findByOrderId(orderId).stream()
            .map(
                x -> {
                  x.setStatus(ReservationStatus.CANCELLED);
                  return x;
                })
            .toList();

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
    }

    inventoryReservationRepository.saveAll(reservations);
    inventoryRepository.saveAll(updatedInventories);
  }

  @Override
  @Transactional
  public void confirmStock(UUID orderId) {
    List<InventoryReservationEntity> reservations =
        inventoryReservationRepository.findByOrderId(orderId);
    List<InventoryEntity> updatedInventories = new ArrayList<>();

    for (InventoryReservationEntity reservation : reservations) {
      // 2. Đổi trạng thái lịch sử sang COMMITTED ✅
      reservation.setStatus(ReservationStatus.COMMITTED);

      // 3. Cập nhật lại kho tổng: Chỉ trừ đi phần đang bị giữ, không đụng đến phần available
      InventoryEntity inventory = reservation.getInventory();
      inventory.setReservedQuantity(
          inventory.getReservedQuantity() - reservation.getReservedQuantity());

      updatedInventories.add(inventory);
    }

    // 4. Lưu đồng loạt xuống Database
    inventoryReservationRepository.saveAll(reservations);
    inventoryRepository.saveAll(updatedInventories);
  }
}
