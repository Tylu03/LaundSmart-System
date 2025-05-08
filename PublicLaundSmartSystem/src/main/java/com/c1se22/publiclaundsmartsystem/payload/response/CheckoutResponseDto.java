package com.c1se22.publiclaundsmartsystem.payload.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutResponseDto {
    private String accountNumber;
    private String accountName;
    private Integer amount;
    private String description;
    private String checkoutUrl;
    private String qrCode;
    private Long orderCode;
    private String status;
}
