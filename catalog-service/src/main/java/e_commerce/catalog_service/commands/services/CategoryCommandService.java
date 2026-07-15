package e_commerce.catalog_service.commands.services;

import e_commerce.catalog_service.commands.dtos.request.CategoryCreateRequest;
import e_commerce.catalog_service.commands.dtos.response.CategoryResponse;
import e_commerce.catalog_service.commands.dtos.request.CategoryUpdateRequest;
import e_commerce.catalog_service.commands.dtos.response.ImportResultResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface CategoryCommandService {
  CategoryResponse createCategory(CategoryCreateRequest request);
  CategoryResponse updateCategory(UUID id, CategoryUpdateRequest request);
  void deleteCategory(UUID id);
  ImportResultResponse importExcelFile(MultipartFile file);
}
