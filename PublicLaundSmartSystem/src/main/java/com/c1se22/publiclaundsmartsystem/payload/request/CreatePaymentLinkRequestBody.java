package com.c1se22.publiclaundsmartsystem.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreatePaymentLinkRequestBody {
    @NotBlank(message = "Product name is required")
    private String productName;
    @NotBlank(message = "Description is required")
    private String description;
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be a positive number")
    private int price;
}
