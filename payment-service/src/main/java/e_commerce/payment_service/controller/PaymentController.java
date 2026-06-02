package e_commerce.payment_service.controller;

import e_commerce.payment_service.dto.*;
import e_commerce.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @GetMapping("/wallets/{userId}")
  public ResponseEntity<WalletResponse> getWalletByUserId(@PathVariable UUID userId) {
    return ResponseEntity.ok(paymentService.getWalletByUserId(userId));
  }

  @PutMapping("/wallets/deposit")
  public ResponseEntity<WalletResponse> deposit(@Valid @RequestBody WalletRequest request) {
    return ResponseEntity.ok(paymentService.createOrUpdateWallet(request));
  }

  @PostMapping("/process")
  public ResponseEntity<TransactionResponse> processPayment(@Valid @RequestBody TransactionRequest request) {
    return ResponseEntity.ok(paymentService.processPayment(request));
  }

  @PostMapping("/refund/{orderId}")
  public ResponseEntity<TransactionResponse> refundPayment(@PathVariable UUID orderId) {
    return ResponseEntity.ok(paymentService.refundPayment(orderId));
  }
}
