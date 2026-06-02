package e_commerce.inventory_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import e_commerce.inventory_service.entity.InventoryEntity;
import e_commerce.inventory_service.listener.CatalogEventListener;
import e_commerce.inventory_service.repository.InventoryRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class CatalogEventListenerTest {

  @Mock
  private InventoryRepository inventoryRepository;

  @Spy
  private ObjectMapper objectMapper = new ObjectMapper();

  @InjectMocks
  private CatalogEventListener catalogEventListener;

  private UUID productId;
  private String createEventJson;

  @BeforeEach
  void setUp() {
    productId = UUID.randomUUID();
    createEventJson = "{\n"
        + "  \"before\": null,\n"
        + "  \"after\": {\n"
        + "    \"id\": \"5641e8d4-d16d-45e4-854f-e54d94af200c\",\n"
        + "    \"aggregate_type\": \"products\",\n"
        + "    \"aggregate_id\": \"" + productId + "\",\n"
        + "    \"event_type\": \"CREATE\",\n"
        + "    \"payload\": \"{}\",\n"
        + "    \"created_at\": 1780064330381809\n"
        + "  }\n"
        + "}";
  }

  @Test
  void handleCatalogEvents_WhenNewProductCreated_InitializesInventory() {
    when(inventoryRepository.existsByProductId(productId)).thenReturn(false);

    catalogEventListener.handleCatalogEvents(createEventJson);

    ArgumentCaptor<InventoryEntity> captor = ArgumentCaptor.forClass(InventoryEntity.class);
    verify(inventoryRepository, times(1)).existsByProductId(productId);
    verify(inventoryRepository, times(1)).save(captor.capture());

    InventoryEntity savedInventory = captor.getValue();
    assertNotNull(savedInventory);
    assertEquals(productId, savedInventory.getProductId());
    assertEquals(0, savedInventory.getAvailableQuantity());
    assertEquals(0, savedInventory.getReservedQuantity());
  }

  @Test
  void handleCatalogEvents_WhenProductAlreadyExists_DoesNotInitialize() {
    when(inventoryRepository.existsByProductId(productId)).thenReturn(true);

    catalogEventListener.handleCatalogEvents(createEventJson);

    verify(inventoryRepository, times(1)).existsByProductId(productId);
    verify(inventoryRepository, never()).save(any(InventoryEntity.class));
  }

  @Test
  void handleCatalogEvents_WhenNotProductEvent_DoesNothing() {
    String categoryEventJson = "{\n"
        + "  \"before\": null,\n"
        + "  \"after\": {\n"
        + "    \"id\": \"5641e8d4-d16d-45e4-854f-e54d94af200c\",\n"
        + "    \"aggregate_type\": \"categories\",\n"
        + "    \"aggregate_id\": \"" + productId + "\",\n"
        + "    \"event_type\": \"CREATE\"\n"
        + "  }\n"
        + "}";

    catalogEventListener.handleCatalogEvents(categoryEventJson);

    verify(inventoryRepository, never()).existsByProductId(any(UUID.class));
    verify(inventoryRepository, never()).save(any(InventoryEntity.class));
  }
}
