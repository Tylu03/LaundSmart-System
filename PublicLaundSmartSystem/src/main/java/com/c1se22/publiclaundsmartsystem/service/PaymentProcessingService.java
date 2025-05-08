package com.c1se22.publiclaundsmartsystem.service;

import com.c1se22.publiclaundsmartsystem.payload.response.CheckoutResponseDto;
import com.c1se22.publiclaundsmartsystem.payload.request.CreatePaymentLinkRequestBody;
import com.c1se22.publiclaundsmartsystem.payload.response.PaymentLinkDto;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface PaymentProcessingService {
    CheckoutResponseDto createPaymentLink(CreatePaymentLinkRequestBody RequestBody);
    PaymentLinkDto getPaymentLinkData(long paymentLinkId);
    PaymentLinkDto cancelPaymentLink(long paymentLinkId);
    ObjectNode confirmWebhook(String webhookUrl);
    void payosTransferHandler(ObjectNode body);
}
