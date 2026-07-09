package e_commerce.search_service.controller;

import e_commerce.common_shared.dtos.PageResponse;
import e_commerce.search_service.document.ProductDocument;
import e_commerce.search_service.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductSearchController {
  private final ProductSearchService productSearchService;

  @GetMapping
//  @Cacheable(value = "cache:products", key = "#keyword + ':' + #page + ':' + #size")
  public PageResponse<ProductDocument> searchProducts(
      @RequestParam String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return productSearchService.searchProducts(
            keyword, page, size);
  }
}
