package e_commerce.search_service.controller;

import e_commerce.common_shared.dtos.PageResponse;
import e_commerce.search_service.document.CategoryDocument;
import e_commerce.search_service.service.CategorySearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
