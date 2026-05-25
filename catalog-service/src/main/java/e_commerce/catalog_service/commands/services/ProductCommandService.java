package e_commerce.catalog_service.commands.services;

import e_commerce.catalog_service.commands.dtos.ProductCreateRequest;
import e_commerce.catalog_service.commands.dtos.ProductUpdateRequest;
import e_commerce.catalog_service.commands.dtos.ProductResponse;
import e_commerce.catalog_service.commands.entities.CategoryEntity;
import e_commerce.catalog_service.commands.entities.ProductEntity;
import e_commerce.catalog_service.commands.repositories.CategoryRepository;
import e_commerce.catalog_service.commands.repositories.ProductRepository;
import e_commerce.catalog_service.exception.ResourceAlreadyExistsException;
import e_commerce.catalog_service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductCommandService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  @Transactional
  public ProductResponse createProduct(ProductCreateRequest request) {
    CategoryEntity category = categoryRepository.findByIdAndIsDeletedFalse(request.getCategoryId())
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));

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

    ProductEntity saved = productRepository.save(product);
    return mapToResponse(saved);
  }

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
    product.setStockQuantity(request.getStockQuantity());

    ProductEntity saved = productRepository.save(product);
    return mapToResponse(saved);
  }

  @Transactional
  public void deleteProduct(UUID id) {
    ProductEntity product = productRepository.findByIdAndIsDeletedFalse(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));

    product.setDeleted(true);
    productRepository.save(product);
  }

  private ProductResponse mapToResponse(ProductEntity entity) {
    return ProductResponse.builder()
        .id(entity.getId())
        .categoryId(entity.getCategory().getId())
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
