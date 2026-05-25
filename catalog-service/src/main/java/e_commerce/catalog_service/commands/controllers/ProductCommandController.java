package e_commerce.catalog_service.commands.controllers;

import e_commerce.catalog_service.commands.dtos.ProductCreateRequest;
import e_commerce.catalog_service.commands.dtos.ProductResponse;
import e_commerce.catalog_service.commands.dtos.ProductUpdateRequest;
import e_commerce.catalog_service.commands.services.ProductCommandService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/catalog/api/v1/products")
@RequiredArgsConstructor
public class ProductCommandController {

  private final ProductCommandService productCommandService;

  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(
      @Valid @RequestBody ProductCreateRequest request) {
    ProductResponse response = productCommandService.createProduct(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductResponse> updateProduct(
      @PathVariable UUID id, @Valid @RequestBody ProductUpdateRequest request) {
    ProductResponse response = productCommandService.updateProduct(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
    productCommandService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }
}
