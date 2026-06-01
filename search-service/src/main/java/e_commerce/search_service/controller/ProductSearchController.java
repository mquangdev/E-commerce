package e_commerce.search_service.controller;

import e_commerce.search_service.document.ProductDocument;
import e_commerce.search_service.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search/products")
@RequiredArgsConstructor
public class ProductSearchController {
  private final ProductSearchRepository productSearchRepository;

  @GetMapping
  @Cacheable(value = "cache:products", key = "#keyword + ':' + #page + ':' + #size")
  public Page<ProductDocument> searchProducts(
      @RequestParam String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
    Pageable pageable = PageRequest.of(page, size).withSort(sort);
    return productSearchRepository.findByNameContainingOrDescriptionContaining(
        keyword, keyword, pageable);
  }
}
