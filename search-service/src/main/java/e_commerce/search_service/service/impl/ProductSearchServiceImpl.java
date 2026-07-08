package e_commerce.search_service.service.impl;

import e_commerce.search_service.document.CategoryDocument;
import e_commerce.search_service.document.ProductDocument;
import e_commerce.search_service.dto.PageResponse;
import e_commerce.search_service.repository.ProductSearchRepository;
import e_commerce.search_service.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {
    private final ProductSearchRepository productSearchRepository;

    @Override
    public PageResponse<ProductDocument> searchProducts(String keyword, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size).withSort(sort);

        Page<ProductDocument> resultPage;

        if (keyword == null || keyword.trim().isEmpty()) {
            resultPage = productSearchRepository.findAll(pageable);
        } else {
            resultPage = productSearchRepository.searchProductsFuzzy(
                    keyword, pageable);
        }

        return PageResponse.of(resultPage);
    }
}
