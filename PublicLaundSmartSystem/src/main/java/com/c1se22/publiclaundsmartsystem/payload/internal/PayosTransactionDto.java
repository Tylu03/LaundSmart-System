package com.c1se22.publiclaundsmartsystem.payload.internal;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayosTransactionDto {
    private String reference;
    private Integer amount;
    private String accountNumber;
    private String description;
    private String transactionDateTime;
}
