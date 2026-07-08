package e_commerce.search_service.service;

import e_commerce.search_service.document.CategoryDocument;
import e_commerce.search_service.document.ProductDocument;
import e_commerce.search_service.dto.PageResponse;

public interface ProductSearchService {
    PageResponse<ProductDocument> searchProducts(String keyword, int page, int size);
}
