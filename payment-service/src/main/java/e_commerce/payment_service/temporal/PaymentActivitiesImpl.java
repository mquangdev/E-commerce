package e_commerce.payment_service.temporal;

import e_commerce.common_shared.exception.BusinessException;
import e_commerce.payment_service.entity.PaymentTransactionEntity;
import e_commerce.payment_service.entity.UserWalletEntity;
import e_commerce.payment_service.enums.TransactionStatus;
import e_commerce.payment_service.repository.PaymentTransactionRepository;
import e_commerce.payment_service.repository.UserWalletRepository;
import java.math.BigDecimal;
import java.util.Optional;
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
  private final PaymentTransactionRepository paymentTransactionRepository;

  @Override
  @Transactional
  public void processPayment(UUID orderId, UUID userId, BigDecimal amount) {
    log.info("[PAYMENT SERVICE] Đang xử lý thanh toán {} VNĐ cho đơn hàng {}", amount, orderId);

    UserWalletEntity wallet =
        walletRepository
            .findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy ví của User: " + userId));

    Optional<PaymentTransactionEntity> existingTransaction =
        paymentTransactionRepository.findByOrderId(orderId);

    if (existingTransaction.isPresent()) {
      log.warn(
          "⚠️ [PAYMENT SERVICE] Đơn hàng {} đã được thanh toán trước đó, bỏ qua xử lý.", orderId);
      return;
    }

    PaymentTransactionEntity paymentTransaction = new PaymentTransactionEntity();
    paymentTransaction.setOrderId(orderId);
    paymentTransaction.setUserId(userId);
    paymentTransaction.setAmount(amount);
    paymentTransaction.setStatus(TransactionStatus.SUCCESS);
    paymentTransaction.setPaymentMethod("WALLET");

    if (wallet.getBalance().compareTo(amount) < 0) {
      log.error("❌ [PAYMENT SERVICE] Khách hàng {} không đủ tiền!", userId);
      throw new BusinessException("Số dư không đủ để thanh toán đơn hàng: " + orderId);
    }

    wallet.setBalance(wallet.getBalance().subtract(amount));

    walletRepository.save(wallet);
    paymentTransactionRepository.save(paymentTransaction);

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

    Optional<PaymentTransactionEntity> existingTransaction =
        paymentTransactionRepository.findByOrderId(orderId);
    if (existingTransaction.isEmpty()) {
      log.warn(
          "⚠️ [PAYMENT SERVICE] Không tìm thấy giao dịch thanh toán cho đơn hàng {}, bỏ qua hoàn tiền.",
          orderId);
      return;
    }

    PaymentTransactionEntity transaction = existingTransaction.get();

    if (transaction.getStatus().equals(TransactionStatus.FAILED)) {
      log.warn(
          "⚠️ [PAYMENT SERVICE] Giao dịch thanh toán cho đơn hàng {} đã ở trạng thái FAILED, bỏ qua hoàn tiền.",
          orderId);
      return;
    }

    transaction.setStatus(TransactionStatus.FAILED);
    paymentTransactionRepository.save(transaction);

    wallet.setBalance(wallet.getBalance().add(amount));
    walletRepository.save(wallet);

    log.info(
        "[PAYMENT SERVICE] Hoàn tiền thanh toán thành công {} VNĐ cho đơn hàng {}",
        amount,
        orderId);
  }
}
