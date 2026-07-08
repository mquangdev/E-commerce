package e_commerce.search_service.service;

import e_commerce.search_service.document.CategoryDocument;
import e_commerce.search_service.dto.PageResponse;

public interface CategorySearchService {
    PageResponse<CategoryDocument> searchCategories(String keyword, int page, int size);
}
