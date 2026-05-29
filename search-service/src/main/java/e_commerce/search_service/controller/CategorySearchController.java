package e_commerce.search_service.controller;

import e_commerce.search_service.document.CategoryDocument;
import e_commerce.search_service.repository.CategorySearchRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search/categories")
@RequiredArgsConstructor
public class CategorySearchController {
  private final CategorySearchRepository categorySearchRepository;

  @GetMapping
  public List<CategoryDocument> searchCategories(@PathVariable String keyword) {
    return categorySearchRepository.findByNameContainingOrDescriptionContaining(keyword, keyword);
  }
}
