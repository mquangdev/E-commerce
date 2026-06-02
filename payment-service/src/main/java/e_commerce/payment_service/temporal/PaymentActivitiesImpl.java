package e_commerce.payment_service.temporal;

import e_commerce.common_shared.exception.BusinessException;
import e_commerce.payment_service.entity.UserWalletEntity;
import e_commerce.payment_service.repository.UserWalletRepository;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentActivitiesImpl implements PaymentActivities {
  private final UserWalletRepository walletRepository;

  @Override
  @Transactional
  public void processPayment(UUID orderId, UUID userId, BigDecimal amount) {
    log.info("[PAYMENT SERVICE] Đang xử lý thanh toán {} VNĐ cho đơn hàng {}", amount, orderId);

    UserWalletEntity wallet =
        walletRepository
            .findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy ví của User: " + userId));

    if (wallet.getBalance().compareTo(amount) < 0) {
      log.error("❌ [PAYMENT SERVICE] Khách hàng {} không đủ tiền!", userId);
      // Ném lỗi để báo cho Nhạc trưởng biết mà Rollback kho
      throw new BusinessException("Số dư không đủ để thanh toán đơn hàng: " + orderId);
    }

    wallet.setBalance(wallet.getBalance().subtract(amount));
    walletRepository.save(wallet);

    log.info("[PAYMENT SERVICE] Thanh toán thành công {} VNĐ cho đơn hàng {}", amount, orderId);
  }

  @Override
  @Transactional
  public void refundPayment(UUID orderId, UUID userId, BigDecimal amount) {
    log.info(
        "[PAYMENT SERVICE] Đang xử lý hoàn tiền thanh toán {} VNĐ cho đơn hàng {}",
        amount,
        orderId);

    UserWalletEntity wallet =
        walletRepository
            .findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy ví của User: " + userId));

    wallet.setBalance(wallet.getBalance().add(amount));
    walletRepository.save(wallet);

    log.info(
        "[PAYMENT SERVICE] Hoàn tiền thanh toán thành công {} VNĐ cho đơn hàng {}",
        amount,
        orderId);
  }
}
