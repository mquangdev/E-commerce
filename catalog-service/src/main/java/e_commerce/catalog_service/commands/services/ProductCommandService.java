package e_commerce.catalog_service.commands.services;

import e_commerce.catalog_service.commands.dtos.request.ProductCreateRequest;
import e_commerce.catalog_service.commands.dtos.request.ProductRecieveMoreInventoryRequest;
import e_commerce.catalog_service.commands.dtos.response.ProductResponse;
import e_commerce.catalog_service.commands.dtos.request.ProductUpdateRequest;
import java.util.UUID;

public interface ProductCommandService {
  ProductResponse createProduct(ProductCreateRequest request);
  ProductResponse updateProduct(UUID id, ProductUpdateRequest request);
  void deleteProduct(UUID id);
  ProductResponse receiveMoreInventory(UUID id, ProductRecieveMoreInventoryRequest request);
}
