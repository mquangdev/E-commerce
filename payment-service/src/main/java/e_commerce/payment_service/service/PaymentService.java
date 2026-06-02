package e_commerce.payment_service.service;

import e_commerce.payment_service.dto.*;
import java.util.UUID;

public interface PaymentService {
  WalletResponse getWalletByUserId(UUID userId);
  WalletResponse createOrUpdateWallet(WalletRequest request);
  TransactionResponse processPayment(TransactionRequest request);
  TransactionResponse refundPayment(UUID orderId);
}
