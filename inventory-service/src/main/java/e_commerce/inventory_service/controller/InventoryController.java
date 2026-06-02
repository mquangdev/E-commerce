package e_commerce.inventory_service.controller;

import e_commerce.inventory_service.dto.InventoryRequest;
import e_commerce.inventory_service.dto.InventoryResponse;
import e_commerce.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {

  private final InventoryService inventoryService;

  @GetMapping("/{productId}")
  public ResponseEntity<InventoryResponse> getInventoryByProductId(@PathVariable UUID productId) {
    InventoryResponse response = inventoryService.getInventoryByProductId(productId);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/stock-in")
  public ResponseEntity<InventoryResponse> stockIn(@Valid @RequestBody InventoryRequest request) {
    InventoryResponse response = inventoryService.createOrUpdateInventory(request);
    return ResponseEntity.ok(response);
  }
}
