package e_commerce.order_service.repository;

import e_commerce.order_service.entity.OrderEntity;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    @Query("SELECT o FROM OrderEntity o WHERE " +
            ":keyword IS NULL OR :keyword = '' OR " +
            "LOWER(o.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(o.shippingAddress) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<OrderEntity> searchOrders(@Param("keyword") String keyword, Pageable pageable);
}
