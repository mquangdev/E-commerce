package e_commerce.search_service.service;

import e_commerce.common_shared.dtos.PageResponse;
import e_commerce.search_service.document.ProductDocument;

public interface ProductSearchService {
    PageResponse<ProductDocument> searchProducts(String keyword, int page, int size);
}
