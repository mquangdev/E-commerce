package e_commerce.search_service.controller;

import e_commerce.search_service.document.CategoryDocument;
import e_commerce.search_service.repository.CategorySearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search/categories")
@RequiredArgsConstructor
public class CategorySearchController {
  private final CategorySearchRepository categorySearchRepository;

  @GetMapping
  @Cacheable(value = "cache:categories", key = "#keyword + ':' + #page + ':' + #size")
  public Page<CategoryDocument> searchCategories(
      @RequestParam String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
    Pageable pageable = PageRequest.of(page, size).withSort(sort);
    return categorySearchRepository.findByNameContainingOrDescriptionContaining(
        keyword, keyword, pageable);
  }
}
