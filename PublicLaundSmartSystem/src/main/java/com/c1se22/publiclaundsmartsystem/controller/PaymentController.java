package com.c1se22.publiclaundsmartsystem.controller;

import com.c1se22.publiclaundsmartsystem.payload.response.CheckoutResponseDto;
import com.c1se22.publiclaundsmartsystem.payload.request.CreatePaymentLinkRequestBody;
import com.c1se22.publiclaundsmartsystem.payload.response.PaymentLinkDto;
import com.c1se22.publiclaundsmartsystem.service.PaymentProcessingService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@AllArgsConstructor
public class PaymentController {
    PaymentProcessingService paymentProcessingService;

    @PostMapping("/create")
    public ResponseEntity<CheckoutResponseDto> createPaymentLink(@RequestBody CreatePaymentLinkRequestBody
                                                                             createPaymentLinkRequestBody) {
        return ResponseEntity.ok(paymentProcessingService.createPaymentLink(createPaymentLinkRequestBody));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentLinkDto> getPaymentLinkDataById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentProcessingService.getPaymentLinkData(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentLinkDto> cancelPaymentLink(@PathVariable Long id) {
        return ResponseEntity.ok(paymentProcessingService.cancelPaymentLink(id));
    }

    @PostMapping("/confirm-webhook")
    public ResponseEntity<ObjectNode> confirmWebhook(@RequestBody Map<String, String> requestBody) {
        return ResponseEntity.ok(paymentProcessingService.confirmWebhook(requestBody.get("webhookUrl")));
    }

    @PostMapping("/transfer-handler")
    public ResponseEntity<ObjectNode> transferHandler(@RequestBody ObjectNode body) {
        paymentProcessingService.payosTransferHandler(body);
        return ResponseEntity.ok().build();
    }
}
