package e_commerce.catalog_service.commands.services.impl;

import e_commerce.catalog_service.commands.dtos.request.OutboxEventRequest;
import e_commerce.catalog_service.commands.dtos.request.ProductCreateRequest;
import e_commerce.catalog_service.commands.dtos.request.ProductRecieveMoreInventoryRequest;
import e_commerce.catalog_service.commands.dtos.response.ProductResponse;
import e_commerce.catalog_service.commands.dtos.request.ProductUpdateRequest;
import e_commerce.catalog_service.commands.entities.CategoryEntity;
import e_commerce.catalog_service.commands.entities.ProductEntity;
import e_commerce.catalog_service.commands.repositories.CategoryRepository;
import e_commerce.catalog_service.commands.repositories.ProductRepository;
import e_commerce.catalog_service.commands.services.OutboxEventService;
import e_commerce.catalog_service.commands.services.ProductCommandService;
import e_commerce.common_shared.exception.ResourceAlreadyExistsException;
import e_commerce.common_shared.exception.ResourceNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductCommandServiceImpl implements ProductCommandService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final OutboxEventService outboxEventService;

  @Override
  @Transactional
  public ProductResponse createProduct(ProductCreateRequest request) {
    CategoryEntity category =
        categoryRepository
            .findByIdAndIsDeletedFalse(request.getCategoryId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Không tìm thấy danh mục với ID: " + request.getCategoryId()));

    if (productRepository.existsBySku(request.getSku())) {
      throw new ResourceAlreadyExistsException("SKU đã tồn tại");
    }

    ProductEntity product = ProductEntity.builder()
        .id(UUID.randomUUID())
        .category(category)
        .sku(request.getSku())
        .name(request.getName())
        .description(request.getDescription())
        .price(request.getPrice())
        .imageUrl(request.getImageUrl())
        .stockQuantity(request.getStockQuantity())
        .build();

    Map<String, Object> eventPayload = new HashMap<>();
    ProductEntity saved = productRepository.save(product);
    eventPayload.put("product", mapToResponse(saved));
    outboxEventService.insertOutboxEvent(
        OutboxEventRequest.builder()
            .eventType("CREATE")
            .aggregateType("products")
            .aggregateId(saved.getId())
            .payloadObj(eventPayload)
            .build()
    );
    return mapToResponse(saved);
  }

  @Override
  @Transactional
  public ProductResponse updateProduct(UUID id, ProductUpdateRequest request) {
    ProductEntity product = productRepository.findByIdAndIsDeletedFalse(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));

    CategoryEntity category = categoryRepository.findByIdAndIsDeletedFalse(request.getCategoryId())
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));

    product.setCategory(category);
    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setImageUrl(request.getImageUrl());

    ProductEntity saved = productRepository.save(product);
    outboxEventService.insertOutboxEvent(
        OutboxEventRequest.builder()
            .eventType("UPDATE")
            .aggregateType("products")
            .aggregateId(saved.getId())
            .payloadObj(mapToResponse(saved))
            .build()
    );
    return mapToResponse(saved);
  }

  @Override
  @Transactional
  public void deleteProduct(UUID id) {
    ProductEntity product = productRepository.findByIdAndIsDeletedFalse(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));

    product.setDeleted(true);
    productRepository.save(product);
    outboxEventService.insertOutboxEvent(
        OutboxEventRequest.builder()
            .eventType("DELETE")
            .aggregateType("products")
            .aggregateId(id)
            .build()
    );
  }

  @Override
  @Transactional
  public ProductResponse receiveMoreInventory(UUID id, ProductRecieveMoreInventoryRequest request) {
    int addedInventory = request.getAddedInventory();
    ProductEntity product = productRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new ResourceNotFoundException("Khôn tìm thấy sản phẩm với ID: " + id));
    int newStockQuantity = product.getStockQuantity() + addedInventory;
    product.setStockQuantity(newStockQuantity);
    ProductEntity saved = productRepository.save(product);

    Map<String, Object> eventPayload = new HashMap<>();
    eventPayload.put("product", mapToResponse(saved));
    eventPayload.put("addedInventory", addedInventory);

    outboxEventService.insertOutboxEvent(
        OutboxEventRequest.builder()
            .eventType("UPDATE")
            .aggregateType("products")
            .aggregateId(id)
            .payloadObj(eventPayload)
            .build()
    );
    return null;
  }

  private ProductResponse mapToResponse(ProductEntity entity) {
    return ProductResponse.builder()
        .id(entity.getId())
        .categoryId(entity.getCategory().getId())
        .categoryName(entity.getCategory().getName())
        .sku(entity.getSku())
        .name(entity.getName())
        .description(entity.getDescription())
        .price(entity.getPrice())
        .imageUrl(entity.getImageUrl())
        .stockQuantity(entity.getStockQuantity())
        .isDeleted(entity.isDeleted())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .version(entity.getVersion())
        .build();
  }
}
