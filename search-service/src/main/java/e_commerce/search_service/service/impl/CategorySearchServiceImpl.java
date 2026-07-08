package e_commerce.search_service.service.impl;

import e_commerce.search_service.document.CategoryDocument;
import e_commerce.search_service.dto.PageResponse;
import e_commerce.search_service.repository.CategorySearchRepository;
import e_commerce.search_service.service.CategorySearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategorySearchServiceImpl implements CategorySearchService {
    private final CategorySearchRepository categorySearchRepository;

    @Override
    public PageResponse<CategoryDocument> searchCategories(String keyword, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size).withSort(sort);

        Page<CategoryDocument> resultPage;

        if (keyword == null || keyword.trim().isEmpty()) {
            resultPage = categorySearchRepository.findAll(pageable);
        } else {
            resultPage = categorySearchRepository.searchCategoriesFuzzy(
                    keyword, pageable);
        }

        return PageResponse.of(resultPage);
    }
}
