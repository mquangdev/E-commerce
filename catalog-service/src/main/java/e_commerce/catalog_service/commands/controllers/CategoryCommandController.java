package e_commerce.catalog_service.commands.controllers;

import e_commerce.catalog_service.commands.dtos.CategoryCreateRequest;
import e_commerce.catalog_service.commands.dtos.CategoryResponse;
import e_commerce.catalog_service.commands.dtos.CategoryUpdateRequest;
import e_commerce.catalog_service.commands.services.CategoryCommandService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/catalog/api/v1/categories")
@RequiredArgsConstructor
public class CategoryCommandController {

  private final CategoryCommandService categoryCommandService;

  @PostMapping
  public ResponseEntity<CategoryResponse> createCategory(
      @Valid @RequestBody CategoryCreateRequest request) {
    CategoryResponse response = categoryCommandService.createCategory(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryResponse> updateCategory(
      @PathVariable UUID id, @Valid @RequestBody CategoryUpdateRequest request) {
    CategoryResponse response = categoryCommandService.updateCategory(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
    categoryCommandService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }
}
