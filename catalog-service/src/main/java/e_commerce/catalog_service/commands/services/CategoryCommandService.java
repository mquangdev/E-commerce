package e_commerce.catalog_service.commands.services;

import e_commerce.catalog_service.commands.dtos.CategoryCreateRequest;
import e_commerce.catalog_service.commands.dtos.CategoryResponse;
import e_commerce.catalog_service.commands.dtos.CategoryUpdateRequest;
import e_commerce.catalog_service.commands.entities.CategoryEntity;
import e_commerce.catalog_service.commands.repositories.CategoryRepository;
import e_commerce.common_shared.exception.ResourceAlreadyExistsException;
import e_commerce.common_shared.exception.ResourceNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryCommandService {

  private final CategoryRepository categoryRepository;
  private final OutboxEventService outboxEventService;

  @Transactional
  public CategoryResponse createCategory(CategoryCreateRequest request) {
    if (categoryRepository.existsByName(request.getName())) {
      throw new ResourceAlreadyExistsException("Tên danh mục đã tồn tại");
    }

    CategoryEntity category =
        CategoryEntity.builder()
            .id(UUID.randomUUID())
            .name(request.getName())
            .description(request.getDescription())
            .isActive(request.getIsActive() != null ? request.getIsActive() : true)
            .build();

    CategoryEntity saved = categoryRepository.save(category);
    outboxEventService.insertOutboxEvent("CREATE", "categories", saved.getId(), mapToResponse(saved));
    return mapToResponse(saved);
  }

  @Transactional
  public CategoryResponse updateCategory(UUID id, CategoryUpdateRequest request) {
    CategoryEntity category =
        categoryRepository
            .findByIdAndIsDeletedFalse(id)
            .orElseThrow(
                () -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

    if (!category.getName().equalsIgnoreCase(request.getName())
        && categoryRepository.existsByName(request.getName())) {
      throw new ResourceAlreadyExistsException("Tên danh mục đã tồn tại");
    }

    category.setName(request.getName());
    category.setDescription(request.getDescription());
    if (request.getIsActive() != null) {
      category.setActive(request.getIsActive());
    }

    CategoryEntity saved = categoryRepository.save(category);
    outboxEventService.insertOutboxEvent("UPDATE", "categories", saved.getId(), mapToResponse(saved));
    return mapToResponse(saved);
  }

  @Transactional
  public void deleteCategory(UUID id) {
    CategoryEntity category =
        categoryRepository
            .findByIdAndIsDeletedFalse(id)
            .orElseThrow(
                () -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

    category.setDeleted(true);
    categoryRepository.save(category);
    outboxEventService.insertOutboxEvent("DELETE", "categories", id, null);
  }

  private CategoryResponse mapToResponse(CategoryEntity entity) {
    return CategoryResponse.builder()
        .id(entity.getId())
        .name(entity.getName())
        .description(entity.getDescription())
        .isActive(entity.isActive())
        .isDeleted(entity.isDeleted())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .version(entity.getVersion())
        .build();
  }
}
