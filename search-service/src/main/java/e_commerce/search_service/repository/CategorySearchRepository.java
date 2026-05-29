package e_commerce.search_service.repository;

import e_commerce.search_service.document.CategoryDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorySearchRepository
    extends ElasticsearchRepository<CategoryDocument, String> {
  List<CategoryDocument> findByNameContainingOrDescriptionContaining(
      String name, String description);
}
