package e_commerce.search_service.service;

import e_commerce.common_shared.dtos.PageResponse;
import e_commerce.search_service.document.CategoryDocument;

public interface CategorySearchService {
    PageResponse<CategoryDocument> searchCategories(String keyword, int page, int size);
}
