package com.c1se22.publiclaundsmartsystem.payload.response;

import com.c1se22.publiclaundsmartsystem.payload.internal.PayosTransactionDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentLinkDto {
    private String id;
    private Long orderCode;
    private Integer amount;
    private Integer amountPaid;
    private Integer amountRemaining;
    private String status;
    private String createdAt;
    private List<PayosTransactionDto> transactions;
    private String cancellationReason;
    private String canceledAt;

}
