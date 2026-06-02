package e_commerce.inventory_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import e_commerce.common_shared.exception.ResourceNotFoundException;
import e_commerce.inventory_service.dto.InventoryRequest;
import e_commerce.inventory_service.dto.InventoryResponse;
import e_commerce.inventory_service.entity.InventoryEntity;
import e_commerce.inventory_service.repository.InventoryRepository;
import e_commerce.inventory_service.service.impl.InventoryServiceImpl;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

  @Mock
  private InventoryRepository inventoryRepository;

  @InjectMocks
  private InventoryServiceImpl inventoryService;

  private UUID productId;
  private InventoryEntity existingInventory;

  @BeforeEach
  void setUp() {
    productId = UUID.randomUUID();
    existingInventory = InventoryEntity.builder()
        .id(UUID.randomUUID())
        .productId(productId)
        .availableQuantity(10)
        .reservedQuantity(2)
        .version(1L)
        .build();
  }

  @Test
  void getInventoryByProductId_WhenExists_ReturnsResponse() {
    when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(existingInventory));

    InventoryResponse response = inventoryService.getInventoryByProductId(productId);

    assertNotNull(response);
    assertEquals(existingInventory.getId(), response.getId());
    assertEquals(productId, response.getProductId());
    assertEquals(10, response.getAvailableQuantity());
    assertEquals(2, response.getReservedQuantity());
    assertEquals(1L, response.getVersion());

    verify(inventoryRepository, times(1)).findByProductId(productId);
  }

  @Test
  void getInventoryByProductId_WhenNotExists_ThrowsResourceNotFoundException() {
    when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
      inventoryService.getInventoryByProductId(productId);
    });

    verify(inventoryRepository, times(1)).findByProductId(productId);
  }

  @Test
  void createOrUpdateInventory_WhenNew_CreatesInventory() {
    UUID newProductId = UUID.randomUUID();
    InventoryRequest request = new InventoryRequest(newProductId, 5);

    when(inventoryRepository.findByProductId(newProductId)).thenReturn(Optional.empty());
    when(inventoryRepository.save(any(InventoryEntity.class))).thenAnswer(invocation -> {
      InventoryEntity entity = invocation.getArgument(0);
      entity.setId(UUID.randomUUID());
      return entity;
    });

    InventoryResponse response = inventoryService.createOrUpdateInventory(request);

    assertNotNull(response);
    assertEquals(newProductId, response.getProductId());
    assertEquals(5, response.getAvailableQuantity());
    assertEquals(0, response.getReservedQuantity());

    verify(inventoryRepository, times(1)).findByProductId(newProductId);
    verify(inventoryRepository, times(1)).save(any(InventoryEntity.class));
  }

  @Test
  void createOrUpdateInventory_WhenExists_UpdatesQuantity() {
    InventoryRequest request = new InventoryRequest(productId, 5);

    when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(existingInventory));
    when(inventoryRepository.save(any(InventoryEntity.class))).thenReturn(existingInventory);

    InventoryResponse response = inventoryService.createOrUpdateInventory(request);

    assertNotNull(response);
    assertEquals(productId, response.getProductId());
    assertEquals(15, response.getAvailableQuantity()); // 10 + 5

    verify(inventoryRepository, times(1)).findByProductId(productId);
    verify(inventoryRepository, times(1)).save(existingInventory);
  }
}
