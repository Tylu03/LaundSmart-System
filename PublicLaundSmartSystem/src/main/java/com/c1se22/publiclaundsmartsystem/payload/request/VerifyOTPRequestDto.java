package com.c1se22.publiclaundsmartsystem.payload.request;

import lombok.Data;

@Data
public class VerifyOTPRequestDto {
    private String otp;
    private String email;
}
