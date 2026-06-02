package e_commerce.payment_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import e_commerce.payment_service.dto.*;
import e_commerce.payment_service.entity.*;
import e_commerce.payment_service.enums.TransactionStatus;
import e_commerce.payment_service.exception.InsufficientBalanceException;
import e_commerce.payment_service.repository.*;
import e_commerce.payment_service.service.impl.PaymentServiceImpl;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @Mock
  private UserWalletRepository walletRepository;

  @Mock
  private PaymentTransactionRepository transactionRepository;

  @InjectMocks
  private PaymentServiceImpl paymentService;

  private UUID userId;
  private UUID orderId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    orderId = UUID.randomUUID();
  }

  @Test
  void getWalletByUserId_WhenWalletExists_ReturnsWalletResponse() {
    UserWalletEntity wallet = UserWalletEntity.builder()
        .id(UUID.randomUUID())
        .userId(userId)
        .balance(BigDecimal.valueOf(100.00))
        .version(1L)
        .build();

    when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

    WalletResponse response = paymentService.getWalletByUserId(userId);

    assertNotNull(response);
    assertEquals(userId, response.getUserId());
    assertEquals(BigDecimal.valueOf(100.00), response.getBalance());
    verify(walletRepository, times(1)).findByUserId(userId);
  }

  @Test
  void createOrUpdateWallet_WhenWalletDoesNotExist_CreatesNewWallet() {
    WalletRequest request = WalletRequest.builder()
        .userId(userId)
        .amount(BigDecimal.valueOf(150.00))
        .build();

    when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());
    when(walletRepository.save(any(UserWalletEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    WalletResponse response = paymentService.createOrUpdateWallet(request);

    assertNotNull(response);
    assertEquals(userId, response.getUserId());
    assertEquals(BigDecimal.valueOf(150.00), response.getBalance());
    verify(walletRepository, times(1)).findByUserId(userId);
    verify(walletRepository, times(1)).save(any(UserWalletEntity.class));
  }

  @Test
  void createOrUpdateWallet_WhenWalletExists_UpdatesBalance() {
    WalletRequest request = WalletRequest.builder()
        .userId(userId)
        .amount(BigDecimal.valueOf(50.00))
        .build();

    UserWalletEntity existingWallet = UserWalletEntity.builder()
        .id(UUID.randomUUID())
        .userId(userId)
        .balance(BigDecimal.valueOf(100.00))
        .version(0L)
        .build();

    when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(existingWallet));
    when(walletRepository.save(any(UserWalletEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    WalletResponse response = paymentService.createOrUpdateWallet(request);

    assertNotNull(response);
    assertEquals(userId, response.getUserId());
    assertEquals(BigDecimal.valueOf(150.00), response.getBalance());
    verify(walletRepository, times(1)).findByUserId(userId);
    verify(walletRepository, times(1)).save(existingWallet);
  }

  @Test
  void processPayment_WhenTransactionExistsAndIsSuccess_ReturnsIdempotentResponse() {
    TransactionRequest request = TransactionRequest.builder()
        .orderId(orderId)
        .userId(userId)
        .amount(BigDecimal.valueOf(50.00))
        .paymentMethod("WALLET")
        .build();

    PaymentTransactionEntity existingTx = PaymentTransactionEntity.builder()
        .id(UUID.randomUUID())
        .orderId(orderId)
        .userId(userId)
        .amount(BigDecimal.valueOf(50.00))
        .paymentMethod("WALLET")
        .status(TransactionStatus.SUCCESS)
        .build();

    when(transactionRepository.findByOrderId(orderId)).thenReturn(Optional.of(existingTx));

    TransactionResponse response = paymentService.processPayment(request);

    assertNotNull(response);
    assertEquals(TransactionStatus.SUCCESS, response.getStatus());
    verify(transactionRepository, times(1)).findByOrderId(orderId);
    verifyNoInteractions(walletRepository);
  }

  @Test
  void processPayment_WhenTransactionExistsAndIsNotSuccess_ThrowsException() {
    TransactionRequest request = TransactionRequest.builder()
        .orderId(orderId)
        .userId(userId)
        .amount(BigDecimal.valueOf(50.00))
        .paymentMethod("WALLET")
        .build();

    PaymentTransactionEntity existingTx = PaymentTransactionEntity.builder()
        .id(UUID.randomUUID())
        .orderId(orderId)
        .userId(userId)
        .amount(BigDecimal.valueOf(50.00))
        .paymentMethod("WALLET")
        .status(TransactionStatus.FAILED)
        .build();

    when(transactionRepository.findByOrderId(orderId)).thenReturn(Optional.of(existingTx));

    assertThrows(IllegalStateException.class, () -> paymentService.processPayment(request));
  }

  @Test
  void processPayment_WhenSuccessful_ChargesWalletAndCreatesSuccessTransaction() {
    TransactionRequest request = TransactionRequest.builder()
        .orderId(orderId)
        .userId(userId)
        .amount(BigDecimal.valueOf(40.00))
        .paymentMethod("WALLET")
        .build();

    UserWalletEntity wallet = UserWalletEntity.builder()
        .id(UUID.randomUUID())
        .userId(userId)
        .balance(BigDecimal.valueOf(100.00))
        .version(0L)
        .build();

    when(transactionRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
    when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
    when(transactionRepository.save(any(PaymentTransactionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    TransactionResponse response = paymentService.processPayment(request);

    assertNotNull(response);
    assertEquals(TransactionStatus.SUCCESS, response.getStatus());
    assertEquals(BigDecimal.valueOf(60.00), wallet.getBalance());
    verify(walletRepository, times(1)).save(wallet);
    verify(transactionRepository, times(1)).save(any(PaymentTransactionEntity.class));
  }

  @Test
  void processPayment_WhenInsufficientBalance_SavesFailedTransactionAndThrowsException() {
    TransactionRequest request = TransactionRequest.builder()
        .orderId(orderId)
        .userId(userId)
        .amount(BigDecimal.valueOf(150.00))
        .paymentMethod("WALLET")
        .build();

    UserWalletEntity wallet = UserWalletEntity.builder()
        .id(UUID.randomUUID())
        .userId(userId)
        .balance(BigDecimal.valueOf(100.00))
        .version(0L)
        .build();

    when(transactionRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
    when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

    assertThrows(InsufficientBalanceException.class, () -> paymentService.processPayment(request));

    ArgumentCaptor<PaymentTransactionEntity> captor = ArgumentCaptor.forClass(PaymentTransactionEntity.class);
    verify(transactionRepository, times(1)).save(captor.capture());
    assertEquals(TransactionStatus.FAILED, captor.getValue().getStatus());
    assertEquals(BigDecimal.valueOf(100.00), wallet.getBalance()); // balance unchanged
  }

  @Test
  void refundPayment_WhenAlreadyRefunded_ReturnsIdempotentResponse() {
    PaymentTransactionEntity refundedTx = PaymentTransactionEntity.builder()
        .id(UUID.randomUUID())
        .orderId(orderId)
        .userId(userId)
        .amount(BigDecimal.valueOf(50.00))
        .paymentMethod("WALLET")
        .status(TransactionStatus.REFUNDED)
        .build();

    when(transactionRepository.findByOrderId(orderId)).thenReturn(Optional.of(refundedTx));

    TransactionResponse response = paymentService.refundPayment(orderId);

    assertNotNull(response);
    assertEquals(TransactionStatus.REFUNDED, response.getStatus());
    verifyNoInteractions(walletRepository);
  }

  @Test
  void refundPayment_WhenSuccessful_RefundsWalletAndUpdatesStatus() {
    PaymentTransactionEntity successTx = PaymentTransactionEntity.builder()
        .id(UUID.randomUUID())
        .orderId(orderId)
        .userId(userId)
        .amount(BigDecimal.valueOf(50.00))
        .paymentMethod("WALLET")
        .status(TransactionStatus.SUCCESS)
        .build();

    UserWalletEntity wallet = UserWalletEntity.builder()
        .id(UUID.randomUUID())
        .userId(userId)
        .balance(BigDecimal.valueOf(100.00))
        .version(0L)
        .build();

    when(transactionRepository.findByOrderId(orderId)).thenReturn(Optional.of(successTx));
    when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
    when(transactionRepository.save(any(PaymentTransactionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    TransactionResponse response = paymentService.refundPayment(orderId);

    assertNotNull(response);
    assertEquals(TransactionStatus.REFUNDED, response.getStatus());
    assertEquals(BigDecimal.valueOf(150.00), wallet.getBalance());
    verify(walletRepository, times(1)).save(wallet);
    verify(transactionRepository, times(1)).save(successTx);
  }
}
