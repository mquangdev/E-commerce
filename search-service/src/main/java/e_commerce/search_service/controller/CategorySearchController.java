package e_commerce.search_service.controller;

import e_commerce.search_service.document.CategoryDocument;
import e_commerce.search_service.dto.PageResponse;
import e_commerce.search_service.repository.CategorySearchRepository;
import e_commerce.search_service.service.CategorySearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategorySearchController {
  private final CategorySearchService categorySearchService;

  @GetMapping
//  @Cacheable(value = "cache:categories", key = "#keyword + ':' + #page + ':' + #size")
  public PageResponse<CategoryDocument> searchCategories(
      @RequestParam String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return categorySearchService.searchCategories(
            keyword, page, size);
  }
}
