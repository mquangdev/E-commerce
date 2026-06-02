package e_commerce.payment_service.repository;

import e_commerce.payment_service.entity.PaymentTransactionEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransactionEntity, UUID> {
  Optional<PaymentTransactionEntity> findByOrderId(UUID orderId);
}
