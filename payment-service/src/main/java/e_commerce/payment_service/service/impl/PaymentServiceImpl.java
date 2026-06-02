package e_commerce.payment_service.service.impl;

import e_commerce.common_shared.exception.ResourceNotFoundException;
import e_commerce.payment_service.dto.*;
import e_commerce.payment_service.entity.*;
import e_commerce.payment_service.enums.TransactionStatus;
import e_commerce.payment_service.exception.InsufficientBalanceException;
import e_commerce.payment_service.repository.*;
import e_commerce.payment_service.service.PaymentService;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

  private final UserWalletRepository walletRepository;
  private final PaymentTransactionRepository transactionRepository;

  @Override
  @Transactional(readOnly = true)
  public WalletResponse getWalletByUserId(UUID userId) {
    UserWalletEntity wallet = walletRepository.findByUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ví của người dùng: " + userId));
    return mapToWalletResponse(wallet);
  }

  @Override
  @Transactional
  public WalletResponse createOrUpdateWallet(WalletRequest request) {
    UserWalletEntity wallet = walletRepository.findByUserId(request.getUserId())
        .orElseGet(() -> UserWalletEntity.builder()
            .userId(request.getUserId())
            .balance(BigDecimal.ZERO)
            .build());

    wallet.setBalance(wallet.getBalance().add(request.getAmount()));
    UserWalletEntity saved = walletRepository.save(wallet);
    return mapToWalletResponse(saved);
  }

  @Override
  @Transactional
  public TransactionResponse processPayment(TransactionRequest request) {
    var existingOpt = transactionRepository.findByOrderId(request.getOrderId());
    if (existingOpt.isPresent()) {
      PaymentTransactionEntity existing = existingOpt.get();
      if (existing.getStatus() == TransactionStatus.SUCCESS) {
        return mapToTransactionResponse(existing);
      }
      throw new IllegalStateException("Đơn hàng này đã có giao dịch với trạng thái: " + existing.getStatus());
    }

    UserWalletEntity wallet = walletRepository.findByUserId(request.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ví của người dùng: " + request.getUserId()));

    if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
      PaymentTransactionEntity failedTx = PaymentTransactionEntity.builder()
          .orderId(request.getOrderId())
          .userId(request.getUserId())
          .amount(request.getAmount())
          .paymentMethod(request.getPaymentMethod())
          .status(TransactionStatus.FAILED)
          .build();
      transactionRepository.save(failedTx);
      throw new InsufficientBalanceException("Số dư ví không đủ để thanh toán đơn hàng: " + request.getOrderId());
    }

    wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
    walletRepository.save(wallet);

    PaymentTransactionEntity transaction = PaymentTransactionEntity.builder()
        .orderId(request.getOrderId())
        .userId(request.getUserId())
        .amount(request.getAmount())
        .paymentMethod(request.getPaymentMethod())
        .status(TransactionStatus.SUCCESS)
        .build();

    PaymentTransactionEntity saved = transactionRepository.save(transaction);
    return mapToTransactionResponse(saved);
  }

  @Override
  @Transactional
  public TransactionResponse refundPayment(UUID orderId) {
    PaymentTransactionEntity transaction = transactionRepository.findByOrderId(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch cho đơn hàng: " + orderId));

    if (transaction.getStatus() == TransactionStatus.REFUNDED) {
      return mapToTransactionResponse(transaction);
    }
    if (transaction.getStatus() != TransactionStatus.SUCCESS) {
      throw new IllegalStateException("Không thể hoàn tiền cho giao dịch có trạng thái: " + transaction.getStatus());
    }

    UserWalletEntity wallet = walletRepository.findByUserId(transaction.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ví của người dùng: " + transaction.getUserId()));

    wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
    walletRepository.save(wallet);

    transaction.setStatus(TransactionStatus.REFUNDED);
    PaymentTransactionEntity saved = transactionRepository.save(transaction);
    return mapToTransactionResponse(saved);
  }

  private WalletResponse mapToWalletResponse(UserWalletEntity entity) {
    return WalletResponse.builder()
        .id(entity.getId())
        .userId(entity.getUserId())
        .balance(entity.getBalance())
        .version(entity.getVersion())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .build();
  }

  private TransactionResponse mapToTransactionResponse(PaymentTransactionEntity entity) {
    return TransactionResponse.builder()
        .id(entity.getId())
        .orderId(entity.getOrderId())
        .userId(entity.getUserId())
        .amount(entity.getAmount())
        .paymentMethod(entity.getPaymentMethod())
        .gatewayTransactionId(entity.getGatewayTransactionId())
        .status(entity.getStatus())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .build();
  }
}
