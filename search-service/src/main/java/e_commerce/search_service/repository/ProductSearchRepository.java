package e_commerce.search_service.repository;

import e_commerce.search_service.document.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {
  Page<ProductDocument> findByNameContainingOrDescriptionContaining(
      String name, String description, Pageable pageable);

  @Query("""
          {
            "multi_match": {
              "query": "?0",
              "fields": ["name", "description"],
              "fuzziness": "AUTO"
            }
          }
          """)
  Page<ProductDocument> searchProductsFuzzy(
          String keyword, Pageable pageable);
}
